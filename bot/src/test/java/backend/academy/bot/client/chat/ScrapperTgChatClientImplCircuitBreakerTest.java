package backend.academy.bot.client.chat;

import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.client.WebClientProperties;
import backend.academy.bot.client.WireMockTestUtil;
import io.github.resilience4j.retry.RetryConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.net.URI;
import java.time.Duration;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import io.github.resilience4j.retry.Retry;
import java.util.function.Supplier;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import static org.assertj.core.api.Assertions.assertThat;


@EnableRetry
public class ScrapperTgChatClientImplCircuitBreakerTest {
    private static final int FIXED_PORT = 8081;
    private static ScrapperTgChatClientImpl originalClient;
    private static ScrapperTgChatClient decoratedClient;
    private static CircuitBreaker circuitBreaker;
    private static Retry retry;

    @BeforeAll
    static void setup() throws Exception {
        // 1. Запуск WireMock
        WireMockTestUtil.setUp(FIXED_PORT);

        // 2. Создание оригинального клиента
        WebClientProperties properties = new WebClientProperties();
        originalClient = new ScrapperTgChatClientImpl(properties);

        RetryConfig retryConfig = RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofSeconds(3))
            .retryExceptions(WebClientResponseException.class)
            .build();
        retry = Retry.of("registerChat", retryConfig);

        CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
            .slidingWindowSize(1)
            .minimumNumberOfCalls(1)
            .failureRateThreshold(100)
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .build();
        circuitBreaker = CircuitBreaker.of("ScrapperChatClient", cbConfig);

        decoratedClient = createDecoratedClient(originalClient, retry, circuitBreaker);
    }

    private static ScrapperTgChatClient createDecoratedClient(
        ScrapperTgChatClientImpl client,
        Retry retry,
        CircuitBreaker circuitBreaker) {

        return new ScrapperTgChatClient() {
            @Override
            public void registerChat(Long tgChatId) {
                Supplier<Void> supplier = () -> {
                    client.registerChat(tgChatId);
                    return null;
                };

                Supplier<Void> decorated = CircuitBreaker.decorateSupplier(
                    circuitBreaker,
                    Retry.decorateSupplier(retry, supplier)
                );

                try {
                    decorated.get();
                } catch (Exception e) {
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    }
                    throw new RuntimeException(e);
                }
            }

            @Override
            public LinkResponse deleteChat(Long tgChatId, RemoveLinkRequest request) {
                Supplier<LinkResponse> supplier = () -> client.deleteChat(tgChatId, request);

                Supplier<LinkResponse> decorated = CircuitBreaker.decorateSupplier(
                    circuitBreaker,
                    Retry.decorateSupplier(retry, supplier)
                );

                try {
                    return decorated.get();
                } catch (Exception e) {
                    if (e instanceof RuntimeException runtimeException) {
                        throw runtimeException;
                    }
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @AfterAll
    static void tearDown() {
        WireMockTestUtil.tearDown();
    }

    @BeforeEach
    void setUpEach() {
        // Создаем новый CircuitBreaker перед каждым тестом
        CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
            .slidingWindowSize(1)
            .minimumNumberOfCalls(1)
            .failureRateThreshold(100)
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .build();
        circuitBreaker = CircuitBreaker.of("ScrapperChatClient", cbConfig);

        decoratedClient = createDecoratedClient(originalClient, retry, circuitBreaker);
    }


    @Test
    @DisplayName("registerChat: CircuitBreaker открывается после 3 неудачных попыток")
    void registerChatShouldOpenCircuitAfterThreeFailures() {
        // Настраиваем постоянные 500 ошибки
        WireMockTestUtil.getWireMockServer().stubFor(post(urlPathMatching("/tg-chat/123"))
            .willReturn(aResponse().withStatus(500)));

        // Первые 3 вызова (должны пройти через Retry)

        assertThrows(WebClientResponseException.class,
            () -> decoratedClient.registerChat(123L));


        // Проверяем что CircuitBreaker открыт
        assertThat(circuitBreaker.getState())
            .isEqualTo(CircuitBreaker.State.OPEN);

        assertThrows(CallNotPermittedException.class,
            () -> decoratedClient.registerChat(123L));

        // Проверяем что было ровно 3 реальных вызова
        WireMockTestUtil.getWireMockServer().verify(3, postRequestedFor(urlPathMatching("/tg-chat/123")));
    }

    @Test
    @DisplayName("deleteChat: CircuitBreaker открывается после 3 неудачных попыток")
    void deleteChatShouldOpenCircuitAfterThreeFailures() {
        // Настраиваем постоянные 500 ошибки
        WireMockTestUtil.getWireMockServer().stubFor(delete(urlPathMatching("/tg-chat/123"))
            .willReturn(aResponse().withStatus(500)));

        // Первые 3 вызова (должны пройти через Retry)

        RemoveLinkRequest request = new RemoveLinkRequest(URI.create("https://github.com"));


        assertThrows(WebClientResponseException.class,
            () -> decoratedClient.deleteChat(123L,request));


        // Проверяем что CircuitBreaker открыт
        assertThat(circuitBreaker.getState())
            .isEqualTo(CircuitBreaker.State.OPEN);



        assertThrows(CallNotPermittedException.class,
            () -> decoratedClient.deleteChat(123L,request));

        // Проверяем что было ровно 3 реальных вызова
        WireMockTestUtil.getWireMockServer().verify(3, deleteRequestedFor(urlPathMatching("/tg-chat/123")));
    }
}
