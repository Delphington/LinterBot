package tracker.stackoverflow;

import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.request.StackOverFlowRequest;
import backend.academy.scrapper.response.StackOverFlowResponse;
import backend.academy.scrapper.tracker.client.StackOverFlowClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.OffsetDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.lang.reflect.Field;
import java.util.function.Function;

class StackOverFlowClientTest {

    @Test
    @DisplayName("Успешный запрос: возвращает корректный ответ")
    public void getFetchDate_ShouldReturnCorrectResponse() throws Exception {

        WebClient webClient = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        // Настраиваем мок
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec); // Исправлено
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // Мок ответа от API
        StackOverFlowResponse mockResponse = new StackOverFlowResponse(List.of(
            new StackOverFlowResponse.ItemResponse(
                12345L,
                "Test Question",
                true,
                2,
                OffsetDateTime.now(),
                OffsetDateTime.now()
            )
        ));
        when(responseSpec.bodyToMono(StackOverFlowResponse.class)).thenReturn(Mono.just(mockResponse));

        ScrapperConfig.StackOverflowCredentials credentials = new ScrapperConfig.StackOverflowCredentials(
            "https://api.stackexchange.com/2.3", // Базовый URL
            null,
            null
        );

        StackOverFlowClient client = new StackOverFlowClient(credentials);
        Field webClientField = StackOverFlowClient.class.getDeclaredField("webClient");
        webClientField.setAccessible(true);
        webClientField.set(client, webClient);

        // Act
        StackOverFlowRequest request = new StackOverFlowRequest("12345");
        StackOverFlowResponse response = client.getFetchDate(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.items().size());
        assertEquals(12345L, response.items().get(0).id());
        assertEquals("Test Question", response.items().get(0).title());

        // Проверяем, что методы мока были вызваны
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(any(Function.class)); // Исправлено
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(StackOverFlowResponse.class);
    }
}
