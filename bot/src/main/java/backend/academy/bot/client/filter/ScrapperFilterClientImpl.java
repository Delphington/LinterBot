package backend.academy.bot.client.filter;

import backend.academy.bot.api.dto.request.filter.FilterRequest;
import backend.academy.bot.api.dto.response.filter.FilterListResponse;
import backend.academy.bot.api.dto.response.filter.FilterResponse;
import backend.academy.bot.client.ErrorResponseHandler;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.WebClientProperties;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ScrapperFilterClientImpl extends ScrapperClient implements ScrapperFilterClient {

    private static final String FILTER_PATH = "/filter/{tgChatId}";

    public ScrapperFilterClientImpl(WebClientProperties webClientProperties) {
        super(webClientProperties);
    }

    // Для работы с фильтрами
    @Retry(name = "createFilter")
    @Override
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
    @Override
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
    @Override
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
