package backend.academy.bot.client.filter;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.academy.bot.api.dto.request.filter.FilterRequest;
import backend.academy.bot.api.dto.response.filter.FilterListResponse;
import backend.academy.bot.api.dto.response.filter.FilterResponse;
import backend.academy.bot.client.WebClientProperties;
import backend.academy.bot.client.WireMockTestUtil;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class ScrapperFilterClientImplCircuitBreakerTest {

    private static final int FIXED_PORT = 8081;
    private static ScrapperFilterClientImpl originalClient;
    private static ScrapperFilterClient decoratedClient;
    private static CircuitBreaker circuitBreaker;
    private static Retry retry;

    @BeforeAll
    static void setup() {
        // 1. Запуск WireMock
        WireMockTestUtil.setUp(FIXED_PORT);

        // 2. Создание оригинального клиента
        WebClientProperties properties = new WebClientProperties();
        originalClient = new ScrapperFilterClientImpl(properties);

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
        circuitBreaker = CircuitBreaker.of("ScrapperFilterClient", cbConfig);

        decoratedClient = createDecoratedClient(originalClient, retry, circuitBreaker);
    }

    private static ScrapperFilterClient createDecoratedClient(
            ScrapperFilterClientImpl client, Retry retry, CircuitBreaker circuitBreaker) {

        return new ScrapperFilterClient() {

            @Override
            public FilterResponse createFilter(Long tgChatId, FilterRequest filterRequest) {
                Supplier<FilterResponse> supplier = () -> client.createFilter(tgChatId, filterRequest);

                Supplier<FilterResponse> decorated =
                        CircuitBreaker.decorateSupplier(circuitBreaker, Retry.decorateSupplier(retry, supplier));

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
            public FilterResponse deleteFilter(Long tgChatId, FilterRequest filterRequest) {
                Supplier<FilterResponse> supplier = () -> client.deleteFilter(tgChatId, filterRequest);

                Supplier<FilterResponse> decorated =
                        CircuitBreaker.decorateSupplier(circuitBreaker, Retry.decorateSupplier(retry, supplier));

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
            public FilterListResponse getFilterList(Long id) {
                Supplier<FilterListResponse> supplier = () -> client.getFilterList(id);

                Supplier<FilterListResponse> decorated =
                        CircuitBreaker.decorateSupplier(circuitBreaker, Retry.decorateSupplier(retry, supplier));

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
        circuitBreaker = CircuitBreaker.of("ScrapperFilterClient", cbConfig);

        decoratedClient = createDecoratedClient(originalClient, retry, circuitBreaker);
    }

    @Test
    @DisplayName("createFilter: CircuitBreaker открывается после 3 неудачных попыток")
    void createFilter_ShouldOpenCircuitAfterThreeFailures() {
        // Настраиваем постоянные 500 ошибки
        WireMockTestUtil.getWireMockServer()
                .stubFor(post(urlPathMatching("/filter/123"))
                        .willReturn(aResponse().withStatus(500)));

        // Первые 3 вызова (должны пройти через Retry)

        assertThrows(
                WebClientResponseException.class,
                () -> decoratedClient.createFilter(123L, new FilterRequest("testFilter")));

        // Проверяем что CircuitBreaker открыт
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        assertThrows(
                CallNotPermittedException.class,
                () -> decoratedClient.createFilter(123L, new FilterRequest("testFilter")));

        // Проверяем что было ровно 3 реальных вызова
        WireMockTestUtil.getWireMockServer().verify(3, postRequestedFor(urlPathMatching("/filter/123")));
    }

    @Test
    @DisplayName("deleteFilter: CircuitBreaker открывается после 3 неудачных попыток")
    void deleteFilter_ShouldOpenCircuitAfterThreeFailures() {
        // Настраиваем постоянные 500 ошибки
        WireMockTestUtil.getWireMockServer()
                .stubFor(delete(urlPathMatching("/filter/123"))
                        .willReturn(aResponse().withStatus(500)));

        // Первые 3 вызова (должны пройти через Retry)

        assertThrows(
                WebClientResponseException.class,
                () -> decoratedClient.deleteFilter(123L, new FilterRequest("testFilter")));

        // Проверяем что CircuitBreaker открыт
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        assertThrows(
                CallNotPermittedException.class,
                () -> decoratedClient.deleteFilter(123L, new FilterRequest("testFilter")));

        // Проверяем что было ровно 3 реальных вызова
        WireMockTestUtil.getWireMockServer().verify(3, deleteRequestedFor(urlPathMatching("/filter/123")));
    }

    @Test
    @DisplayName("getFilterList: CircuitBreaker открывается после 3 неудачных попыток")
    void getFilterList_ShouldOpenCircuitAfterThreeFailures() {
        // Настраиваем постоянные 500 ошибки
        WireMockTestUtil.getWireMockServer()
                .stubFor(get(urlPathMatching("/filter/123"))
                        .willReturn(aResponse().withStatus(500)));

        // Первые 3 вызова (должны пройти через Retry)

        assertThrows(WebClientResponseException.class, () -> decoratedClient.getFilterList(123L));

        // Проверяем что CircuitBreaker открыт
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        assertThrows(CallNotPermittedException.class, () -> decoratedClient.getFilterList(123L));

        // Проверяем что было ровно 3 реальных вызова
        WireMockTestUtil.getWireMockServer().verify(3, getRequestedFor(urlPathMatching("/filter/123")));
    }
}
