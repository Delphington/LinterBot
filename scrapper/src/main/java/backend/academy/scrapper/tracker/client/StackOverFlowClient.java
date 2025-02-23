package backend.academy.scrapper.tracker.client;

import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.request.StackOverFlowRequest;
import backend.academy.scrapper.response.StackOverFlowResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public class StackOverFlowClient {

    private final WebClient webClient;

    public StackOverFlowClient(ScrapperConfig.StackOverflowCredentials stackOverflowCredentials) {
        WebClient.Builder webClientBuilder = WebClient.builder()
            .baseUrl(stackOverflowCredentials.stackOverFlowUrl()); // Убедитесь, что baseUrl корректен

        // Добавляем заголовки key и access-token
        if (stackOverflowCredentials.key() != null && !stackOverflowCredentials.key().isEmpty()) {
            webClientBuilder.defaultHeader("key", stackOverflowCredentials.key());
        }
        if (stackOverflowCredentials.accessToken() != null && !stackOverflowCredentials.accessToken().isEmpty()) {
            webClientBuilder.defaultHeader("access_token", stackOverflowCredentials.accessToken());
        }

        this.webClient = webClientBuilder.build();
    }

    public StackOverFlowResponse getFetchDate(StackOverFlowRequest request) {
        log.info("StackOverFlowClient getFetchDate {}", request);
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/questions/{id}") // Используем правильный путь
                .queryParam("order", request.order())
                .queryParam("sort", request.sort())
                .queryParam("site", request.site())
                .build(request.number())) // Передаем number как параметр пути
            .retrieve()
            .bodyToMono(StackOverFlowResponse.class)
            .block();
    }
}
