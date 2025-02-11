package backend.academy.scrapper;

import backend.academy.scrapper.api.dto.request.LinkUpdatesRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class TelegramBotClient {

    private RestClient restClient;

    public TelegramBotClient(
        RestClient.Builder restClientBuilder,
        @Value("${app.link.telegram-bot-uri}") String baseUrl
    ) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }
//
//    public void addUpdate(LinkUpdatesRequest linkRequest) {
//        restClient.post()
//            .uri("/updates")
//            .contentType(APPLICATION_JSON)
//            .body(linkRequest)
//            .retrieve()
//            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
//                throw new ResponseException(response.getStatusCode().toString());
//            })
//            .toBodilessEntity();
//    }

}
