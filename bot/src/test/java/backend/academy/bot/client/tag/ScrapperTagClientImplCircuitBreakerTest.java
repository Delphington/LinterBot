package backend.academy.bot.client.tag;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.academy.bot.api.dto.request.tag.TagLinkRequest;
import backend.academy.bot.api.dto.request.tag.TagRemoveRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.api.dto.response.TagListResponse;
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

        originalClient = new ScrapperTagClientImpl(webClientProps, webServiceProps);

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

    private static ScrapperTagClient createDecoratedClient(
            ScrapperTagClientImpl client, Retry retry, CircuitBreaker circuitBreaker) {

        return new ScrapperTagClient() {

            @Override
            public ListLinksResponse getListLinksByTag(Long tgChatId, TagLinkRequest tagLinkRequest) {
                Supplier<ListLinksResponse> supplier = () -> client.getListLinksByTag(tgChatId, tagLinkRequest);

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

            @Override
            public TagListResponse getAllListLinksByTag(Long tgChatId) {
                Supplier<TagListResponse> supplier = () -> client.getAllListLinksByTag(tgChatId);

                Supplier<TagListResponse> decorated =
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
            public LinkResponse removeTag(Long tgChatId, TagRemoveRequest tg) {
                Supplier<LinkResponse> supplier = () -> client.removeTag(tgChatId, tg);

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
        WireMockTestUtil.getWireMockServer()
                .stubFor(get(urlPathMatching("/tag/123")).willReturn(aResponse().withStatus(500)));

        // Первые 3 вызова (должны пройти через Retry)
        TagLinkRequest tagLinkRequest = new TagLinkRequest("testTag");

        assertThrows(WebClientResponseException.class, () -> decoratedClient.getListLinksByTag(123L, tagLinkRequest));

        // Проверяем что CircuitBreaker открыт
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        assertThrows(CallNotPermittedException.class, () -> decoratedClient.getListLinksByTag(123L, tagLinkRequest));

        // Проверяем что было ровно 3 реальных вызова
        WireMockTestUtil.getWireMockServer().verify(3, getRequestedFor(urlPathMatching("/tag/123")));
    }

    @Test
    @DisplayName("getAllListLinksByTag: CircuitBreaker открывается после 3 неудачных попыток")
    void getAllListLinksByTag_ShouldOpenCircuitAfterThreeFailures() {
        // Настраиваем постоянные 500 ошибки
        WireMockTestUtil.getWireMockServer()
                .stubFor(get(urlPathMatching("/tag/123")).willReturn(aResponse().withStatus(500)));

        // Первые 3 вызова (должны пройти через Retry)
        assertThrows(WebClientResponseException.class, () -> decoratedClient.getAllListLinksByTag(123L));

        // Проверяем что CircuitBreaker открыт
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        assertThrows(CallNotPermittedException.class, () -> decoratedClient.getAllListLinksByTag(123L));

        // Проверяем что было ровно 3 реальных вызова
        WireMockTestUtil.getWireMockServer().verify(3, getRequestedFor(urlPathMatching("/tag/123")));
    }

    @Test
    @DisplayName("removeTag: CircuitBreaker открывается после 3 неудачных попыток")
    void removeTag_ShouldOpenCircuitAfterThreeFailures() {
        // Настраиваем постоянные 500 ошибки
        WireMockTestUtil.getWireMockServer()
                .stubFor(delete(urlPathMatching("/tag/123"))
                        .willReturn(aResponse().withStatus(500)));

        // Первые 3 вызова (должны пройти через Retry)
        TagRemoveRequest tagRemoveRequest = new TagRemoveRequest("testTag", URI.create("https://github.com"));

        assertThrows(WebClientResponseException.class, () -> decoratedClient.removeTag(123L, tagRemoveRequest));

        // Проверяем что CircuitBreaker открыт
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        assertThrows(CallNotPermittedException.class, () -> decoratedClient.removeTag(123L, tagRemoveRequest));

        // Проверяем что было ровно 3 реальных вызова
        WireMockTestUtil.getWireMockServer().verify(3, deleteRequestedFor(urlPathMatching("/tag/123")));
    }
}
