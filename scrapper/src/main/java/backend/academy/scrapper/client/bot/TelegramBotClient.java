package backend.academy.scrapper.client.bot;

import backend.academy.scrapper.api.dto.response.ApiErrorResponse;
import backend.academy.scrapper.client.tracker.UpdateLinkResponse;
import backend.academy.scrapper.client.exception.ResponseException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class TelegramBotClient {

    private final WebClient webClient;

    public TelegramBotClient(
        final WebClient.Builder webClientBuilder,
        @Value("${app.link.telegram-bot-uri}") String baseUrl
    ) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public void addUpdate(UpdateLinkResponse linkRequest) {
        webClient.post()
            .uri("/updates")
            .contentType(APPLICATION_JSON)
            .body(Mono.just(linkRequest), UpdateLinkResponse.class)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(apiErrorResponse -> Mono.error(new BadRequestException(apiErrorResponse.description()))))
            .toBodilessEntity();
    }

}
