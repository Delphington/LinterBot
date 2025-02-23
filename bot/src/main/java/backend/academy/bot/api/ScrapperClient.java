package backend.academy.bot.api;

import backend.academy.bot.api.dto.request.AddLinkRequest;
import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.api.exception.ResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public final class ScrapperClient {

    private static final String TG_CHAT_PATH = "tg-chat/{id}";
    private static final String LINK_PATH = "links/{tgChatId}";

    private final WebClient webClient;

    public ScrapperClient(
        final WebClient.Builder webClientBuilder,
        final @Value("${app.link.scrapper-uri}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public void registerChat(final Long tgChatId) {
        log.info("ScrapperClient registerChat {} ", tgChatId);

        webClient.post()
            .uri(uriBuilder -> uriBuilder.path(TG_CHAT_PATH).build(tgChatId))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> {
                return response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        String errorMessage = "Ошибка сервера регистрации: "
                                              + response.statusCode()
                                              + ", Body: " + errorBody;
                        log.error(errorMessage);
                        return Mono.error(new ResponseException
                            (response.statusCode().toString()));
                    });
            })
            .bodyToMono(Void.class)
            .block();
    }

    public LinkResponse deleteLink(final Long tgChatId,
                                   final RemoveLinkRequest request) {
        log.info("ScrapperClient deleteLink {} ", tgChatId);

        return webClient.method(HttpMethod.DELETE)
            .uri(LINK_PATH, tgChatId)
            .body(Mono.just(request), RemoveLinkRequest.class)
            .retrieve()
            .bodyToMono(LinkResponse.class)
            .block();
    }


    public LinkResponse trackLink(final Long tgChatId,
                                  final AddLinkRequest request) {

        log.info("ScrapperClient trackLink {} ", tgChatId);

        return webClient.post()
            .uri(uriBuilder -> uriBuilder.path(LINK_PATH).build(tgChatId))
            .header("Tg-Chat-Id", String.valueOf(tgChatId))
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), AddLinkRequest.class)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> {
                return response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        String errorMessage = "Ошибка добавления ссылки "
                                              + response.statusCode()
                                              + ", Body: " + errorBody;
                        log.error(errorMessage);
                        return Mono.error(
                            new ResponseException(response.statusCode().toString()));
                    });
            })
            .bodyToMono(LinkResponse.class)
            .block();
    }




    public LinkResponse untrackLink(final Long tgChatId, final RemoveLinkRequest request) {
        log.info("ScrapperClient untrackLink {} ", tgChatId);

        return webClient.method(HttpMethod.DELETE)
            .uri(uriBuilder -> uriBuilder.path(LINK_PATH).build(tgChatId))
            .header("Tg-Chat-Id", String.valueOf(tgChatId))
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), RemoveLinkRequest.class)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> {
                return response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        String errorMessage = "Ошибка удаления ссылки: "
                                              + response.statusCode()
                                              + ", Body: " + errorBody;
                        log.error(errorMessage);
                        return Mono.error(
                            new ResponseException(response.statusCode().toString()));
                    });
            })
            .bodyToMono(LinkResponse.class)
            .block();
    }


    public ListLinksResponse getListLink(final Long tgChatId) {
        log.info("ScrapperClient getListLink {} ", tgChatId);

        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("links").build())
            .header("Tg-Chat-Id", String.valueOf(tgChatId))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> {
                return response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        String errorMessage = "Ошибка удаления ссылки: "
                                              + response.statusCode()
                                              + ", Body: " + errorBody;
                        log.error(errorMessage);
                        return Mono.error(
                            new ResponseException(response.statusCode().toString()));
                    });
            })
            .bodyToMono(ListLinksResponse.class)
            .block();
    }
}
