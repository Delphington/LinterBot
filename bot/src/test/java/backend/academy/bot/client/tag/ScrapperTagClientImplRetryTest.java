package backend.academy.bot.client.tag;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.academy.bot.api.dto.request.tag.TagLinkRequest;
import backend.academy.bot.api.dto.request.tag.TagRemoveRequest;
import backend.academy.bot.api.dto.response.ApiErrorResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.HelperUtils;
import backend.academy.bot.client.WebClientProperties;
import backend.academy.bot.client.WebServiceProperties;
import backend.academy.bot.client.WireMockTestUtil;
import com.github.tomakehurst.wiremock.common.Json;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class ScrapperTagClientImplRetryTest {

    private static final int FIXED_PORT = 8081;
    private static ScrapperTagClientImpl client;
    private static Retry retry;

    @BeforeAll
    static void setup() {
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

        client = new ScrapperTagClientImpl(webClientProps, webServiceProps);

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
    }

    @AfterAll
    static void tearDown() {
        WireMockTestUtil.tearDown();
    }

    @Test
    @DisplayName("getListLinksByTag: Обработка исключения Server")
    void getListLinksByTag_shouldSuccessWhenServerReturnsError() {
        WireMockTestUtil.getWireMockServer()
                .stubFor(get(urlPathMatching("/tag/123"))
                        .willReturn(aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        assertThrows(
                WebClientResponseException.class, () -> client.getListLinksByTag(123L, new TagLinkRequest("some tag")));
    }

    @Test
    @DisplayName("getListLinksByTag: Обработка исключения ResponseException именно ошибки Scrapper")
    void getListLinksByTag_shouldSuccessWhenServerReturnsOk() {
        ApiErrorResponse errorResponse =
                new ApiErrorResponse("Invalid request", "400", "BadRequestException", "Invalid chat ID", List.of());

        // Настраиваем WireMock для возврата 400 с телом ошибки
        WireMockTestUtil.getWireMockServer()
                .stubFor(get(urlPathMatching("/tag/123"))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.BAD_REQUEST.value())
                                .withHeader("Content-Type", "application/json")
                                .withBody(Json.write(errorResponse))));

        assertThrows(ResponseException.class, () -> client.getListLinksByTag(123L, new TagLinkRequest("some tag")));
    }

    @Test
    @DisplayName("getAllListLinksByTag: Обработка исключения Server")
    void getAllListLinksByTag_shouldSuccessWhenServerReturnsError() {
        WireMockTestUtil.getWireMockServer()
                .stubFor(get(urlPathMatching("/tag/123/all"))
                        .willReturn(aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        assertThrows(WebClientResponseException.class, () -> client.getAllListLinksByTag(123L));
    }

    @Test
    @DisplayName("getAllListLinksByTag: Обработка исключения ResponseException именно ошибки Scrapper")
    void getAllListLinksByTag_shouldSuccessWhenServerReturnsOk() {
        ApiErrorResponse errorResponse =
                new ApiErrorResponse("Invalid request", "400", "BadRequestException", "Invalid chat ID", List.of());

        // Настраиваем WireMock для возврата 400 с телом ошибки
        WireMockTestUtil.getWireMockServer()
                .stubFor(get(urlPathMatching("/tag/123/all"))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.BAD_REQUEST.value())
                                .withHeader("Content-Type", "application/json")
                                .withBody(Json.write(errorResponse))));

        assertThrows(ResponseException.class, () -> client.getAllListLinksByTag(123L));
    }

    @Test
    @DisplayName("removeTag: Обработка исключения Server")
    void removeTag_shouldSuccessWhenServerReturnsError() {
        WireMockTestUtil.getWireMockServer()
                .stubFor(delete(urlPathMatching("/tag/123"))
                        .willReturn(aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        assertThrows(
                WebClientResponseException.class,
                () -> client.removeTag(123L, new TagRemoveRequest("Some", URI.create("http://github.com"))));
    }

    @Test
    @DisplayName("removeTag: Обработка исключения ResponseException именно ошибки Scrapper")
    void removeTag_shouldSuccessWhenServerReturnsOk() {
        ApiErrorResponse errorResponse =
                new ApiErrorResponse("Invalid request", "400", "BadRequestException", "Invalid chat ID", List.of());

        // Настраиваем WireMock для возврата 400 с телом ошибки
        WireMockTestUtil.getWireMockServer()
                .stubFor(delete(urlPathMatching("/tag/123"))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.BAD_REQUEST.value())
                                .withHeader("Content-Type", "application/json")
                                .withBody(Json.write(errorResponse))));

        assertThrows(
                ResponseException.class,
                () -> client.removeTag(123L, new TagRemoveRequest("Some", URI.create("http://github.com"))));
    }
}
