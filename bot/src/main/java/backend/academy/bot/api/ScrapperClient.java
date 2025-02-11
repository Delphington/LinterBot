package backend.academy.bot.api;

import backend.academy.bot.api.dto.request.AddLinkRequest;
import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Log4j2
@Service
public class ScrapperClient {
    private WebClient webClient;

    private String tgChatPath = "tg-chat/{id}";
    private String linkPath = "links/{tgChatId}";

    public ScrapperClient(
        WebClient.Builder webClientBuilder,
        @Value("${app.link.scrapper-uri}") String baseUrl
    ) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public void registerChat(Long id) {
        log.info("====== FROM ScapperClient(tgbot) Registered id  = " + id);

        webClient.post()
            .uri(uriBuilder -> uriBuilder.path(tgChatPath).build(id))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> {
                return response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        String errorMessage = "Ошибка сервера регистрации: " + response.statusCode() + ", Body: " + errorBody;
                        log.error(errorMessage);
                        return Mono.error(new ResponseException(response.statusCode().toString()));
                    });
            })
            .bodyToMono(Void.class)
            .block();

    }

    //todo:
    public LinkResponse deleteLink(Long tgChatId, RemoveLinkRequest request) {
        return webClient.method(HttpMethod.DELETE)
            .uri(linkPath, tgChatId)
            .body(Mono.just(request), RemoveLinkRequest.class)
            .retrieve()
            .bodyToMono(LinkResponse.class)
            .block();
    }


    public LinkResponse trackLink(Long tgChatId, AddLinkRequest request) {

        log.info("====== FROM ScapperClient(tgbot) trackLink id  = " + tgChatId);

        return webClient.post()
            .uri(uriBuilder -> uriBuilder.path(linkPath).build(tgChatId))
            .header("Tg-Chat-Id", String.valueOf(tgChatId)) // Add Tg-Chat-Id header
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), AddLinkRequest.class) // Отправляем AddLinkRequest в теле запроса.
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> {
                return response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        String errorMessage = "Ошибка добавления ссылки " + response.statusCode() + ", Body: " + errorBody;
                        log.error(errorMessage);
                        return Mono.error(new ResponseException(response.statusCode().toString()));
                    });
            })
            .bodyToMono(LinkResponse.class) // Читаем тело ответа и преобразуем его в LinkResponse.
            .block();
    }

    //
    public LinkResponse untrackLink(Long tgChatId, RemoveLinkRequest request) {
        log.info("====== FROM ScapperClient(tgbot) untrackLink id  = " + tgChatId);

        return webClient.method(HttpMethod.DELETE)
            .uri(uriBuilder -> uriBuilder.path(linkPath).build(tgChatId)) // Use path variable for tgChatId
            .header("Tg-Chat-Id", String.valueOf(tgChatId))
            .contentType(MediaType.APPLICATION_JSON) // Set content type
            .body(Mono.just(request), RemoveLinkRequest.class) // Send RemoveLinkRequest in body
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> {
                return response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        String errorMessage = "Ошибка удаления ссылки: " + response.statusCode() + ", Body: " + errorBody;
                        log.error(errorMessage);
                        return Mono.error(new ResponseException(response.statusCode().toString()));
                    });
            })
            .bodyToMono(LinkResponse.class)
            .block();
    }


    public ListLinksResponse getListLink(Long tgChatId) {
        log.info("====== FROM ScapperClient(tgbot) getListLink id  = " + tgChatId);

        return   webClient.get()
            .uri(uriBuilder -> uriBuilder.path("links").build())
            .header("Tg-Chat-Id", String.valueOf(tgChatId)) // Pass tgChatId in the header
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> {
                return response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        String errorMessage = "Ошибка удаления ссылки: " + response.statusCode() + ", Body: " + errorBody;
                        log.error(errorMessage);
                        return Mono.error(new ResponseException(response.statusCode().toString()));
                    });
            })
            .bodyToMono(ListLinksResponse.class)
            .block();


    }
}
