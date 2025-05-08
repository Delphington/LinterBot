package backend.academy.bot.client.tag;

import backend.academy.bot.api.dto.request.tag.TagLinkRequest;
import backend.academy.bot.api.dto.request.tag.TagRemoveRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.api.dto.response.TagListResponse;
import backend.academy.bot.client.WebClientProperties;
import backend.academy.bot.client.WireMockTestUtil;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.net.URI;
import java.time.Duration;
import java.util.function.Supplier;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScrapperTagClientImplCircuitBreakerTest {


    private static final int FIXED_PORT = 8081;
    private static ScrapperTagClientImpl originalClient;
    private static ScrapperTagClient decoratedClient;
    private static CircuitBreaker circuitBreaker;
    private static Retry retry;

    @BeforeAll
    static void setup() {
        // 1. Запуск WireMock
        WireMockTestUtil.setUp(FIXED_PORT);

        // 2. Создание оригинального клиента
        WebClientProperties properties = new WebClientProperties();
        originalClient = new ScrapperTagClientImpl(properties);

        RetryConfig retryConfig = RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofSeconds(3))
            .retryExceptions(WebClientResponseException.class)
            .build();
        retry = Retry.of("testRetry", retryConfig);

        CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
            .slidingWindowSize(1)
            .minimumNumberOfCalls(1)
            .failureRateThreshold(100)
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .build();
        circuitBreaker = CircuitBreaker.of("ScrapperTagClient", cbConfig);

        decoratedClient = createDecoratedClient(originalClient, retry, circuitBreaker);
    }

    private static ScrapperTagClient createDecoratedClient(
        ScrapperTagClientImpl client,
        Retry retry,
        CircuitBreaker circuitBreaker) {

        return new ScrapperTagClient() {

            @Override
            public ListLinksResponse getListLinksByTag(Long tgChatId, TagLinkRequest tagLinkRequest) {
                Supplier<ListLinksResponse> supplier = () -> client.getListLinksByTag(tgChatId, tagLinkRequest);

                Supplier<ListLinksResponse> decorated = CircuitBreaker.decorateSupplier(
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

            @Override
            public TagListResponse getAllListLinksByTag(Long tgChatId) {
                Supplier<TagListResponse> supplier = () -> client.getAllListLinksByTag(tgChatId);

                Supplier<TagListResponse> decorated = CircuitBreaker.decorateSupplier(
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
                }            }

            @Override
            public LinkResponse removeTag(Long tgChatId, TagRemoveRequest tg) {
                Supplier<LinkResponse> supplier = () -> client.removeTag(tgChatId, tg);

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
                }            }


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
        circuitBreaker = CircuitBreaker.of("ScrapperTagClient", cbConfig);

        decoratedClient = createDecoratedClient(originalClient, retry, circuitBreaker);
    }


    @Test
    @DisplayName("getListLinksByTag: CircuitBreaker открывается после 3 неудачных попыток")
    void getListLinksByTag_ShouldOpenCircuitAfterThreeFailures() {
        // Настраиваем постоянные 500 ошибки
        WireMockTestUtil.getWireMockServer().stubFor(get(urlPathMatching("/tag/123"))
            .willReturn(aResponse().withStatus(500)));

        // Первые 3 вызова (должны пройти через Retry)
        TagLinkRequest tagLinkRequest = new TagLinkRequest("testTag");

        assertThrows(WebClientResponseException.class,
            () -> decoratedClient.getListLinksByTag(123L, tagLinkRequest));


        // Проверяем что CircuitBreaker открыт
        assertThat(circuitBreaker.getState())
            .isEqualTo(CircuitBreaker.State.OPEN);

        assertThrows(CallNotPermittedException.class,
            () -> decoratedClient.getListLinksByTag(123L,tagLinkRequest));

        // Проверяем что было ровно 3 реальных вызова
        WireMockTestUtil.getWireMockServer().verify(3, getRequestedFor(urlPathMatching("/tag/123")));
    }

    @Test
    @DisplayName("getAllListLinksByTag: CircuitBreaker открывается после 3 неудачных попыток")
    void getAllListLinksByTag_ShouldOpenCircuitAfterThreeFailures() {
        // Настраиваем постоянные 500 ошибки
        WireMockTestUtil.getWireMockServer().stubFor(get(urlPathMatching("/tag/123"))
            .willReturn(aResponse().withStatus(500)));

        // Первые 3 вызова (должны пройти через Retry)
        assertThrows(WebClientResponseException.class,
            () -> decoratedClient.getAllListLinksByTag(123L));


        // Проверяем что CircuitBreaker открыт
        assertThat(circuitBreaker.getState())
            .isEqualTo(CircuitBreaker.State.OPEN);

        assertThrows(CallNotPermittedException.class,
            () -> decoratedClient.getAllListLinksByTag(123L));

        // Проверяем что было ровно 3 реальных вызова
        WireMockTestUtil.getWireMockServer().verify(3, getRequestedFor(urlPathMatching("/tag/123")));
    }


    @Test
    @DisplayName("removeTag: CircuitBreaker открывается после 3 неудачных попыток")
    void removeTag_ShouldOpenCircuitAfterThreeFailures() {
        // Настраиваем постоянные 500 ошибки
        WireMockTestUtil.getWireMockServer().stubFor(delete(urlPathMatching("/tag/123"))
            .willReturn(aResponse().withStatus(500)));

        // Первые 3 вызова (должны пройти через Retry)
        TagRemoveRequest tagRemoveRequest = new TagRemoveRequest("testTag", URI.create("https://github.com"));

        assertThrows(WebClientResponseException.class,
            () -> decoratedClient.removeTag(123L, tagRemoveRequest));


        // Проверяем что CircuitBreaker открыт
        assertThat(circuitBreaker.getState())
            .isEqualTo(CircuitBreaker.State.OPEN);

        assertThrows(CallNotPermittedException.class,
            () -> decoratedClient.removeTag(123L,tagRemoveRequest));

        // Проверяем что было ровно 3 реальных вызова
        WireMockTestUtil.getWireMockServer().verify(3, deleteRequestedFor(urlPathMatching("/tag/123")));
    }
}
