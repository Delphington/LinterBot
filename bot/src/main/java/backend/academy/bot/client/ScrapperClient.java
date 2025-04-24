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
import io.github.resilience4j.retry.annotation.Retry;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Component
public final class ScrapperClient {

    private static final String TG_CHAT_PATH = "tg-chat/{chatId}";
    private static final String LINK_PATH = "links/{tgChatId}";
    private static final String TAG_PATH = "tag/{tgChatId}";
    private static final String ALL_ELEMENTS_PATH = "/all";
    private static final String FILTER_PATH = "/filter/{tgChatId}";

    private final WebClient webClient;
    private final WebClientProperties wcp;

    public ScrapperClient(
            final WebClient.Builder webClientBuilder,
            final @Value("${app.link.scrapper-uri}") String baseUrl,
            WebClientProperties webClientProperties) {
        this.wcp = webClientProperties;

        // Настраиваем таймауты через HttpClient
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(webClientProperties.responseTimeout()) // Таймаут на ответ
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int)
                        webClientProperties.connectTimeout().toMillis());

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Retry(name = "registerChat")
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
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    @Retry(name = "deleteLink")
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
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    @Retry(name = "trackLink")
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
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    @Retry(name = "untrackLink")
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
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    @Retry(name = "untrackLink")
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
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    // Для тегов
    @Retry(name = "getListLinksByTag")
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
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    @Retry(name = "getAllListLinksByTag")
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
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    @Retry(name = "removeTag")
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
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    // Для работы с фильтрами
    @Retry(name = "createFilter")
    public FilterResponse createFilter(Long chatId, FilterRequest filterRequest) {
        log.info("ScrapperClient addFilter: tgChatId={}, filter={}", chatId, filterRequest.filter());
        return webClient
                .method(HttpMethod.POST)
                .uri(uriBuilder -> uriBuilder.path(FILTER_PATH + "/create").build(chatId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(filterRequest), FilterRequest.class)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        ErrorResponseHandler.handleClientError("Ошибка Добавление фильтра"))
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        ErrorResponseHandler.handleServerError("Ошибка Добавление фильтра"))
                .bodyToMono(FilterResponse.class)
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    @Retry(name = "deleteFilter")
    public FilterResponse deleteFilter(Long tgChatId, FilterRequest filterRequest) {
        log.info("ScrapperClient deleteFilter: tgChatId={}, filter={}", tgChatId, filterRequest.filter());
        log.info("Удаление фильтра для чата {}, фильтр: {}", tgChatId, filterRequest.filter());
        return webClient
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder.path(FILTER_PATH + "/delete").build(tgChatId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(filterRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ErrorResponseHandler.handleClientError("Удаление фильтра"))
                .onStatus(HttpStatusCode::is5xxServerError, ErrorResponseHandler.handleServerError("Удаление фильтра"))
                .bodyToMono(FilterResponse.class)
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    @Retry(name = "getFilterList")
    public FilterListResponse getFilterList(Long id) {
        log.info("ScrapperClient getFilterList: tgChatId={}", id);
        return webClient
                .method(HttpMethod.GET)
                .uri(uriBuilder -> uriBuilder.path(FILTER_PATH).build(id))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError, ErrorResponseHandler.handleClientError("Добавление фильтра"))
                .onStatus(
                        HttpStatusCode::is5xxServerError, ErrorResponseHandler.handleServerError("Добавление фильтра"))
                .bodyToMono(FilterListResponse.class)
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }
}
