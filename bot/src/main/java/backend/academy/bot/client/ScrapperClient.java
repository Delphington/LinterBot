package backend.academy.bot.client;

import backend.academy.bot.api.dto.request.AddLinkRequest;
import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.request.filter.FilterRequest;
import backend.academy.bot.api.dto.request.tag.TagLinkRequest;
import backend.academy.bot.api.dto.request.tag.TagRemoveRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.api.dto.response.TagListResponse;
import backend.academy.bot.api.dto.response.filter.FilterListResponse;
import backend.academy.bot.api.dto.response.filter.FilterResponse;
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

    private static final String TG_CHAT_PATH = "tg-chat/{chatId}";
    private static final String LINK_PATH = "links/{tgChatId}";
    private static final String TAG_PATH = "tag/{tgChatId}";
    private static final String ALL_ELEMENTS_PATH = "/all";
    private static final String FILTER_PATH = "/filter/{tgChatId}";

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
            .onStatus(
                HttpStatusCode::is4xxClientError,
                ErrorResponseHandler.handleClientError("Ошибка добавление ссылки"))
            .onStatus(
                HttpStatusCode::is5xxServerError,
                ErrorResponseHandler.handleServerError("Ошибка добавление ссылки"))
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
            .onStatus(
                HttpStatusCode::is4xxClientError,
                ErrorResponseHandler.handleClientError("Ошибка удаление ссылки"))
            .onStatus(
                HttpStatusCode::is5xxServerError,
                ErrorResponseHandler.handleServerError("Ошибка удаление ссылки"))
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
            .onStatus(
                HttpStatusCode::is4xxClientError,
                ErrorResponseHandler.handleClientError("Ошибка добавление ссылки"))
            .onStatus(
                HttpStatusCode::is5xxServerError,
                ErrorResponseHandler.handleServerError("Ошибка добавление ссылки"))
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
            .onStatus(
                HttpStatusCode::is4xxClientError,
                ErrorResponseHandler.handleClientError("Ошибка уд списка ссылок"))
            .onStatus(
                HttpStatusCode::is5xxServerError,
                ErrorResponseHandler.handleServerError("Ошибка получении списка ссылок"))
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
            .onStatus(
                HttpStatusCode::is4xxClientError,
                ErrorResponseHandler.handleClientError("Ошибка получении списка ссылок"))
            .onStatus(
                HttpStatusCode::is5xxServerError,
                ErrorResponseHandler.handleServerError("Ошибка получении списка ссылок"))
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
            .onStatus(
                HttpStatusCode::is4xxClientError,
                ErrorResponseHandler.handleClientError("Ошибка получении списка ссылок"))
            .onStatus(
                HttpStatusCode::is5xxServerError,
                ErrorResponseHandler.handleServerError("Ошибка получении списка ссылок"))
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
            .onStatus(
                HttpStatusCode::is4xxClientError,
                ErrorResponseHandler.handleClientError("получении списка ссылок"))
            .onStatus(
                HttpStatusCode::is5xxServerError,
                ErrorResponseHandler.handleServerError("получении списка ссылок"))
            .bodyToMono(TagListResponse.class)
            .block();
    }

    public LinkResponse removeTag(Long tgChatId, TagRemoveRequest tg) {
        log.info("ScrapperClient untrackLink: tgChatId={}, request={}", tgChatId, tg);
        return webClient
            .method(HttpMethod.DELETE)
            .uri(uriBuilder -> uriBuilder.path(TAG_PATH).build(tgChatId))
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(tg), TagRemoveRequest.class)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, ErrorResponseHandler.handleClientError("удалении тега"))
            .onStatus(HttpStatusCode::is5xxServerError, ErrorResponseHandler.handleServerError("удалении тега"))
            .bodyToMono(LinkResponse.class)
            .block();
    }

    //Для работы с фильтрами

    public FilterResponse createFilter(FilterRequest filterRequest) {
        log.info("ScrapperClient addFilter: tgChatId={}, filter={}", filterRequest.chatId(), filterRequest.filter());
        return webClient
            .method(HttpMethod.POST)
            .uri(uriBuilder -> uriBuilder.path(FILTER_PATH + "/create").build(filterRequest.chatId()))
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(filterRequest), FilterRequest.class)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, ErrorResponseHandler.handleClientError("Ошибка Добавление фильтра"))
            .onStatus(HttpStatusCode::is5xxServerError, ErrorResponseHandler.handleServerError("Ошибка Добавление фильтра"))
            .bodyToMono(FilterResponse.class)
            .block();
    }

    public FilterResponse deleteFilter(FilterRequest filterRequest) {
        log.info("ScrapperClient deleteFilter: tgChatId={}, filter={}", filterRequest.chatId(), filterRequest.filter());
        return webClient
            .method(HttpMethod.DELETE)
            .uri(uriBuilder -> uriBuilder.path(FILTER_PATH + "/delete").build(filterRequest.chatId()))
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(filterRequest), FilterRequest.class)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, ErrorResponseHandler.handleClientError("Добавление фильтра"))
            .onStatus(HttpStatusCode::is5xxServerError, ErrorResponseHandler.handleServerError("Добавление фильтра"))
            .bodyToMono(FilterResponse.class)
            .block();
    }

    public FilterListResponse getFilterList(Long id) {
        log.info("ScrapperClient getFilterList: tgChatId={}", id);
        return webClient
            .method(HttpMethod.GET)
            .uri(uriBuilder -> uriBuilder.path(FILTER_PATH).build(id))
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, ErrorResponseHandler.handleClientError("Добавление фильтра"))
            .onStatus(HttpStatusCode::is5xxServerError, ErrorResponseHandler.handleServerError("Добавление фильтра"))
            .bodyToMono(FilterListResponse.class)
            .block();
    }
}
