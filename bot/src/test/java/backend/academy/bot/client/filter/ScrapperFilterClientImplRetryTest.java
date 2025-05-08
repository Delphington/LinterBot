package backend.academy.bot.client.filter;

import backend.academy.bot.api.dto.request.filter.FilterRequest;
import backend.academy.bot.api.dto.response.ApiErrorResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.WebClientProperties;
import backend.academy.bot.client.WireMockTestUtil;
import com.github.tomakehurst.wiremock.common.Json;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.Duration;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScrapperFilterClientImplRetryTest {

    private static final int FIXED_PORT = 8081;
    private static ScrapperFilterClientImpl client;
    private static Retry retry;

    @BeforeAll
    static void setup() {
        WireMockTestUtil.setUp(FIXED_PORT);
        WebClientProperties properties = new WebClientProperties();
        client = new ScrapperFilterClientImpl(properties);
        try {
            var field = ScrapperClient.class.getDeclaredField("baseUrl");
            field.setAccessible(true);
            field.set(client, "http://localhost:" + FIXED_PORT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        RetryConfig config = RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofSeconds(1))
            .retryExceptions(CallNotPermittedException.class)
            .build();

        retry = Retry.of("testRetry", config);
    }

    @AfterAll
    static void tearDown() {
        WireMockTestUtil.tearDown();
    }


    @Test
    @DisplayName("createFilter: Обработка исключения Server")
    void createFilter_shouldSuccessWhenServerReturnsError() {
        WireMockTestUtil.getWireMockServer().stubFor(post(urlPathMatching("/filter/123"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        FilterRequest filterRequest = new FilterRequest("Some Filter");

        assertThrows(WebClientResponseException.class,
            () -> client.createFilter(123L, filterRequest));
    }



    @Test
    @DisplayName("createFilter: Обработка исключения ResponseException именно ошибки Scrapper")
    void createFilter_shouldSuccessWhenServerReturnsOk() {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            "Invalid request",
            "400",
            "BadRequestException",
            "Invalid chat ID",
            List.of()
        );

        // Настраиваем WireMock для возврата 400 с телом ошибки
        WireMockTestUtil.getWireMockServer().stubFor(post(urlPathMatching("/filter/123"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withHeader("Content-Type", "application/json")
                .withBody(Json.write(errorResponse))));

        FilterRequest filterRequest = new FilterRequest("Some Filter");

        assertThrows(ResponseException.class,
            () -> client.createFilter(123L,filterRequest));
    }


    @Test
    @DisplayName("deleteFilter: Обработка исключения Server")
    void deleteFilter_shouldSuccessWhenServerReturnsError() {
        WireMockTestUtil.getWireMockServer().stubFor(delete(urlPathMatching("/filter/123"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        FilterRequest filterRequest = new FilterRequest("Some Filter");

        assertThrows(WebClientResponseException.class,
            () -> client.deleteFilter(123L, filterRequest));
    }



    @Test
    @DisplayName("deleteFilter: Обработка исключения ResponseException именно ошибки Scrapper")
    void deleteFilter_shouldSuccessWhenServerReturnsOk() {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            "Invalid request",
            "400",
            "BadRequestException",
            "Invalid chat ID",
            List.of()
        );

        // Настраиваем WireMock для возврата 400 с телом ошибки
        WireMockTestUtil.getWireMockServer().stubFor(delete(urlPathMatching("/filter/123"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withHeader("Content-Type", "application/json")
                .withBody(Json.write(errorResponse))));

        FilterRequest filterRequest = new FilterRequest("Some Filter");

        assertThrows(ResponseException.class,
            () -> client.deleteFilter(123L,filterRequest));
    }


    @Test
    @DisplayName("getFilterList: Обработка исключения Server")
    void getFilterList_shouldSuccessWhenServerReturnsError() {
        WireMockTestUtil.getWireMockServer().stubFor(get(urlPathMatching("/filter/123"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        assertThrows(WebClientResponseException.class,
            () -> client.getFilterList(123L));
    }



    @Test
    @DisplayName("getFilterList: Обработка исключения ResponseException именно ошибки Scrapper")
    void getFilterList_shouldSuccessWhenServerReturnsOk() {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            "Invalid request",
            "400",
            "BadRequestException",
            "Invalid chat ID",
            List.of()
        );

        // Настраиваем WireMock для возврата 400 с телом ошибки
        WireMockTestUtil.getWireMockServer().stubFor(get(urlPathMatching("/filter/123"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withHeader("Content-Type", "application/json")
                .withBody(Json.write(errorResponse))));

        assertThrows(ResponseException.class,
            () -> client.getFilterList(123L));
    }
}
