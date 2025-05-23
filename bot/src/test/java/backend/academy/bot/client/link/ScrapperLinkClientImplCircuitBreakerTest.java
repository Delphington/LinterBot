package backend.academy.bot.client.link;

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

import backend.academy.bot.api.dto.request.AddLinkRequest;
import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.client.HelperUtils;
import backend.academy.bot.client.WebClientProperties;
import backend.academy.bot.client.WebServiceProperties;
import backend.academy.bot.client.WireMockTestUtil;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class ScrapperLinkClientImplCircuitBreakerTest {

    private static final int FIXED_PORT = 8081;
    private static ScrapperLinkClientImpl originalClient;
    private static ScrapperLinkClient decoratedClient;
    private static CircuitBreaker circuitBreaker;
    private static Retry retry;

    @BeforeAll
    static void setup() {
        // 1. Запуск WireMock
        WireMockTestUtil.setUp(FIXED_PORT);

        // 2. Загрузка конфигурации из YAML
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application-test.yaml"));
        Properties properties = yaml.getObject();

        // 3. Создание свойств с конфигурацией
        WebClientProperties webClientProps = new WebClientProperties();
        webClientProps.connectTimeout(Duration.parse(properties.getProperty("app.webclient.timeouts.connect-timeout")));
        webClientProps.responseTimeout(
                Duration.parse(properties.getProperty("app.webclient.timeouts.response-timeout")));
        webClientProps.globalTimeout(Duration.parse(properties.getProperty("app.webclient.timeouts.global-timeout")));

        WebServiceProperties webServiceProps = new WebServiceProperties();
        webServiceProps.scrapperUri(properties.getProperty("app.link.scrapper-uri"));

        originalClient = new ScrapperLinkClientImpl(webClientProps, webServiceProps);

        // 4. Инициализация Resilience4j из конфигурации
        // Retry конфигурация
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(
                        Integer.parseInt(properties.getProperty("resilience4j.retry.configs.default.max-attempts")))
                .waitDuration(HelperUtils.parseDuration(
                        properties.getProperty("resilience4j.retry.configs.default.wait-duration")))
                .retryExceptions(WebClientResponseException.class) // Можно расширить список
                .build();
        retry = Retry.of("registerChat", retryConfig);

        // CircuitBreaker конфигурация
        CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
                .slidingWindowSize(Integer.parseInt(
                        properties.getProperty("resilience4j.circuitbreaker.configs.default.sliding-window-size")))
                .minimumNumberOfCalls(Integer.parseInt(
                        properties.getProperty("resilience4j.circuitbreaker.configs.default.minimum-number-of-calls")))
                .failureRateThreshold(Float.parseFloat(
                        properties.getProperty("resilience4j.circuitbreaker.configs.default.failure-rate-threshold")))
                .waitDurationInOpenState(HelperUtils.parseDuration(properties.getProperty(
                        "resilience4j.circuitbreaker.configs.default.wait-duration-in-open-state")))
                .build();
        circuitBreaker = CircuitBreaker.of("ScrapperChatClient", cbConfig);

        decoratedClient = createDecoratedClient(originalClient, retry, circuitBreaker);
    }

    private static ScrapperLinkClient createDecoratedClient(
            ScrapperLinkClientImpl client, Retry retry, CircuitBreaker circuitBreaker) {

        return new ScrapperLinkClient() {

            @Override
            public LinkResponse trackLink(Long tgChatId, AddLinkRequest request) {
                Supplier<LinkResponse> supplier = () -> client.trackLink(tgChatId, request);

                Supplier<LinkResponse> decorated =
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
            public LinkResponse untrackLink(Long tgChatId, RemoveLinkRequest request) {
                Supplier<LinkResponse> supplier = () -> client.untrackLink(tgChatId, request);

                Supplier<LinkResponse> decorated =
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
            public ListLinksResponse getListLink(Long tgChatId) {
                Supplier<ListLinksResponse> supplier = () -> client.getListLink(tgChatId);

                Supplier<ListLinksResponse> decorated =
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
        circuitBreaker = CircuitBreaker.of("ScrapperLinkClient", cbConfig);

        decoratedClient = createDecoratedClient(originalClient, retry, circuitBreaker);
    }

    @Test
    @DisplayName("trackLink: CircuitBreaker открывается после 3 неудачных попыток")
    void trackLink_ShouldOpenCircuitAfterThreeFailures() {
        // Настраиваем постоянные 500 ошибки
        WireMockTestUtil.getWireMockServer()
                .stubFor(post(urlPathMatching("/links/123"))
                        .willReturn(aResponse().withStatus(500)));

        // Первые 3 вызова (должны пройти через Retry)
        AddLinkRequest addLinkRequest =
                new AddLinkRequest(URI.create("https://github.com"), Collections.emptyList(), Collections.emptyList());

        assertThrows(WebClientResponseException.class, () -> decoratedClient.trackLink(123L, addLinkRequest));

        // Проверяем что CircuitBreaker открыт
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        assertThrows(CallNotPermittedException.class, () -> decoratedClient.trackLink(123L, addLinkRequest));

        // Проверяем что было ровно 3 реальных вызова
        WireMockTestUtil.getWireMockServer().verify(3, postRequestedFor(urlPathMatching("/links/123")));
    }

    @Test
    @DisplayName("untrackLink: CircuitBreaker открывается после 3 неудачных попыток")
    void untrackLink_ShouldOpenCircuitAfterThreeFailures() {
        // Настраиваем постоянные 500 ошибки
        WireMockTestUtil.getWireMockServer()
                .stubFor(delete(urlPathMatching("/links/123"))
                        .willReturn(aResponse().withStatus(500)));

        // Первые 3 вызова (должны пройти через Retry)
        assertThrows(
                WebClientResponseException.class,
                () -> decoratedClient.untrackLink(123L, new RemoveLinkRequest(URI.create("https://github.com"))));

        // Проверяем что CircuitBreaker открыт
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        assertThrows(
                CallNotPermittedException.class,
                () -> decoratedClient.untrackLink(123L, new RemoveLinkRequest(URI.create("https://github.com"))));

        // Проверяем что было ровно 3 реальных вызова
        WireMockTestUtil.getWireMockServer().verify(3, deleteRequestedFor(urlPathMatching("/links/123")));
    }

    @Test
    @DisplayName("getListLink: CircuitBreaker открывается после 3 неудачных попыток")
    void getListLink_ShouldOpenCircuitAfterThreeFailures() {
        // Настраиваем постоянные 500 ошибки
        WireMockTestUtil.getWireMockServer()
                .stubFor(get(urlPathMatching("/links")).willReturn(aResponse().withStatus(500)));

        // Первые 3 вызова (должны пройти через Retry)
        assertThrows(WebClientResponseException.class, () -> decoratedClient.getListLink(123L));

        // Проверяем что CircuitBreaker открыт
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        assertThrows(CallNotPermittedException.class, () -> decoratedClient.getListLink(123L));

        // Проверяем что было ровно 3 реальных вызова
        WireMockTestUtil.getWireMockServer().verify(3, getRequestedFor(urlPathMatching("/links")));
    }
}
