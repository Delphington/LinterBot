package client.http;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.academy.scrapper.client.type.HttpUpdateSender;
import backend.academy.scrapper.configuration.api.WebClientProperties;
import backend.academy.scrapper.tracker.update.model.LinkUpdate;
import client.WireMockTestUtil;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class HttpUpdateSenderRetryTest {

    private static final int FIXED_PORT = 8080;
    private static HttpUpdateSender client;
    private static Retry retry;

    @BeforeAll
    static void setup() {
        WireMockTestUtil.setUp(FIXED_PORT);
        WebClientProperties properties = new WebClientProperties();
        client = new HttpUpdateSender("http://localhost:8080", properties);

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
    @DisplayName("sendUpdate: Обработка исключения Server")
    void sendUpdate_shouldSuccessWhenServerReturnsError() {
        WireMockTestUtil.getWireMockServer()
                .stubFor(post(urlPathMatching("/updates"))
                        .willReturn(aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        assertThrows(
                WebClientResponseException.class,
                () -> client.sendUpdate(new LinkUpdate(
                        1L, URI.create("https://github.com"), "test description", Collections.emptyList())));
    }
}
