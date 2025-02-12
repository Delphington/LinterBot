package backend.academy.scrapper.client.bot;

import backend.academy.scrapper.client.tracker.UpdateLinkResponse;
import backend.academy.scrapper.client.exception.ResponseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class TelegramBotClient {

    //todo: Нужно использовать новый
    private RestClient restClient;

    public TelegramBotClient(
        RestClient.Builder restClientBuilder,
        @Value("${app.link.telegram-bot-uri}") String baseUrl
    ) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public void addUpdate(UpdateLinkResponse linkRequest) {
        restClient.post()
            .uri("/updates")
            .contentType(APPLICATION_JSON)
            .body(linkRequest)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                throw new ResponseException(response.getStatusCode().toString());
            })
            .toBodilessEntity();
    }

}
