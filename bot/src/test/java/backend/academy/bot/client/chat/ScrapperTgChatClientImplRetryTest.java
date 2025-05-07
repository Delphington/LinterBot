package backend.academy.bot.client.chat;

import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.ApiErrorResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.WebClientProperties;
import backend.academy.bot.client.WireMockTestUtil;
import com.github.tomakehurst.wiremock.common.Json;
import io.github.resilience4j.retry.RetryConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

@EnableRetry
public class ScrapperTgChatClientImplRetryTest {
    private static final int FIXED_PORT = 8081;
    private static ScrapperTgChatClientImpl client;
    private static Retry retry;

    @BeforeAll
    static void setup() {
        WireMockTestUtil.setUp(FIXED_PORT);
        WebClientProperties properties = new WebClientProperties();
        client = new ScrapperTgChatClientImpl(properties);
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
    @DisplayName("registerChat: Обработка исключения Server")
    void registerChat_shouldSuccessWhenServerReturnsError() {
        WireMockTestUtil.getWireMockServer().stubFor(post(urlPathMatching("/tg-chat/123"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        assertThrows(WebClientResponseException.class,
            () -> client.registerChat(123L));
    }


    @Test
    @DisplayName("registerChat: Обработка исключения ResponseException именно ошибки Scrapper")
    void registerChat_shouldSuccessWhenServerReturnsOk() {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            "Invalid request",
            "400",
            "BadRequestException",
            "Invalid chat ID",
            List.of()
        );

        // Настраиваем WireMock для возврата 400 с телом ошибки
        WireMockTestUtil.getWireMockServer().stubFor(post(urlPathMatching("/tg-chat/123"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withHeader("Content-Type", "application/json")
                .withBody(Json.write(errorResponse))));

        assertThrows(ResponseException.class,
            () -> client.registerChat(123L));
    }


    @Test
    @DisplayName("deleteChat: Обработка исключения Server")
    void deleteChat_shouldSuccessWhenServerReturnsError() {
        WireMockTestUtil.getWireMockServer().stubFor(delete(urlPathMatching("/tg-chat/123"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        RemoveLinkRequest request = new RemoveLinkRequest(URI.create("https://github.com"));

        assertThrows(WebClientResponseException.class,
            () -> client.deleteChat(123L, request));
    }


    @Test
    @DisplayName("deleteChat: Обработка исключения ResponseException именно ошибки Scrapper")
    void deleteChat_shouldSuccessWhenServerReturnsOk() {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            "Invalid request",
            "400",
            "BadRequestException",
            "Invalid chat ID",
            List.of()
        );

        // Настраиваем WireMock для возврата 400 с телом ошибки
        WireMockTestUtil.getWireMockServer().stubFor(delete(urlPathMatching("/tg-chat/123"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withHeader("Content-Type", "application/json")
                .withBody(Json.write(errorResponse))));

        RemoveLinkRequest request = new RemoveLinkRequest(URI.create("https://github.com"));

        assertThrows(ResponseException.class,
            () -> client.deleteChat(123L, request));
    }

}
