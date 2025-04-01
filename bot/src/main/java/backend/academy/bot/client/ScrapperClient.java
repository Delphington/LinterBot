package backend.academy.bot.client;

import backend.academy.bot.api.dto.request.AddLinkRequest;
import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.request.tag.TagLinkRequest;
import backend.academy.bot.api.dto.request.tag.TagRemoveRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.api.dto.response.TagListResponse;
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
    private static final String TAG_PATH = "tag/{tgChatId}";
    private static final String ALL_ELEMENTS_PATH = "/all";

    private final WebClient webClient;

    public ScrapperClient(
            final WebClient.Builder webClientBuilder, final @Value("${app.link.scrapper-uri}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public void registerChat(final Long tgChatId) {
        log.info("ScrapperClient registerChat {} ", tgChatId);

        webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(TG_CHAT_PATH).build(tgChatId))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    return response.bodyToMono(String.class).flatMap(errorBody -> {
                        String errorMessage =
                                "Ошибка сервера регистрации: " + response.statusCode() + ", Body: " + errorBody;
                        log.error(errorMessage);
                        return Mono.error(
                                new ResponseException(response.statusCode().toString()));
                    });
                })
                .bodyToMono(Void.class)
                .block();
    }

    public LinkResponse deleteLink(final Long tgChatId, final RemoveLinkRequest request) {
        log.info("ScrapperClient deleteLink {} ", tgChatId);

        return webClient
                .method(HttpMethod.DELETE)
                .uri(LINK_PATH, tgChatId)
                .body(Mono.just(request), RemoveLinkRequest.class)
                .retrieve()
                .bodyToMono(LinkResponse.class)
                .block();
    }

    public LinkResponse trackLink(final Long tgChatId, final AddLinkRequest request) {

        log.info("ScrapperClient trackLink {} ", tgChatId);

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(LINK_PATH).build(tgChatId))
                .header("Tg-Chat-Id", String.valueOf(tgChatId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), AddLinkRequest.class)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    return response.bodyToMono(String.class).flatMap(errorBody -> {
                        String errorMessage =
                                "Ошибка добавления ссылки " + response.statusCode() + ", Body: " + errorBody;
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

        return webClient
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder.path(LINK_PATH).build(tgChatId))
                .header("Tg-Chat-Id", String.valueOf(tgChatId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), RemoveLinkRequest.class)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    return response.bodyToMono(String.class).flatMap(errorBody -> {
                        String errorMessage =
                                "Ошибка удаления ссылки: " + response.statusCode() + ", Body: " + errorBody;
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

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("links").build())
                .header("Tg-Chat-Id", String.valueOf(tgChatId))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    return response.bodyToMono(String.class).flatMap(errorBody -> {
                        String errorMessage =
                                "Ошибка удаления ссылки: " + response.statusCode() + ", Body: " + errorBody;
                        log.error(errorMessage);
                        return Mono.error(
                                new ResponseException(response.statusCode().toString()));
                    });
                })
                .bodyToMono(ListLinksResponse.class)
                .block();
    }

    // Для тегов
    public ListLinksResponse getListLinksByTag(Long tgChatId, TagLinkRequest tagLinkRequest) {
        log.info("ScrapperClient getListLinksByTag {} ", tgChatId);

        return webClient
                .method(HttpMethod.GET)
                .uri(uriBuilder -> uriBuilder.path(TAG_PATH).build(tgChatId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(tagLinkRequest), TagLinkRequest.class)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    return response.bodyToMono(String.class).flatMap(errorBody -> {
                        String errorMessage =
                                "Ошибка удаления ссылки: " + response.statusCode() + ", Body: " + errorBody;
                        log.error(errorMessage);
                        return Mono.error(
                                new ResponseException(response.statusCode().toString()));
                    });
                })
                .bodyToMono(ListLinksResponse.class)
                .block();
    }

    public TagListResponse getAllListLinksByTag(Long tgChatId) {
        return webClient
                .method(HttpMethod.GET)
                .uri(uriBuilder -> uriBuilder
                        .path(TAG_PATH + ALL_ELEMENTS_PATH) // Путь будет "tag/{tgChatId}/all"
                        .build(tgChatId))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    return response.bodyToMono(String.class).flatMap(errorBody -> {
                        String errorMessage =
                                "Ошибка при получении списка ссылок: " + response.statusCode() + ", Body: " + errorBody;
                        log.error(errorMessage);
                        return Mono.error(new ResponseException(errorMessage));
                    });
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    return response.bodyToMono(String.class).flatMap(errorBody -> {
                        String errorMessage = "Серверная ошибка при получении списка ссылок: " + response.statusCode()
                                + ", Body: " + errorBody;
                        log.error(errorMessage);
                        return Mono.error(new ResponseException(errorMessage));
                    });
                })
                .bodyToMono(TagListResponse.class)
                .block();
    }

    public LinkResponse removeTag(Long tgChatId, TagRemoveRequest tg) {
        log.info("ScrapperClient untrackLink: tgChatId={}, request={}", tgChatId, tg);
        return webClient
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path(TAG_PATH) // Путь, например "tag/{tgChatId}"
                        .build(tgChatId)) // Передаем tgChatId как часть пути
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(tg), TagRemoveRequest.class)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    return response.bodyToMono(String.class).flatMap(errorBody -> {
                        String errorMessage =
                                "Ошибка удаления ссылки: " + response.statusCode() + ", Body: " + errorBody;
                        log.error(errorMessage);
                        return Mono.error(new ResponseException(errorMessage));
                    });
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    return response.bodyToMono(String.class).flatMap(errorBody -> {
                        String errorMessage = "Серверная ошибка при удалении ссылки: " + response.statusCode()
                                + ", Body: " + errorBody;
                        log.error(errorMessage);
                        return Mono.error(new ResponseException(errorMessage));
                    });
                })
                .bodyToMono(LinkResponse.class)
                .block(); // Блокируем выполнение, чтобы вернуть объект LinkResponse
    }
}

//.onStatus(HttpStatusCode::is4xxClientError, ErrorHandler.handleClientError("Ошибка удаления ссылки"))
//    .onStatus(HttpStatusCode::is5xxServerError, ErrorHandler.handleServerError("Серверная ошибка при удалении ссылки"))
