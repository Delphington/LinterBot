package backend.academy.bot.client.tag;

import backend.academy.bot.api.dto.request.tag.TagLinkRequest;
import backend.academy.bot.api.dto.request.tag.TagRemoveRequest;
import backend.academy.bot.api.dto.response.ApiErrorResponse;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.api.dto.response.TagListResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.WebClientProperties;
import backend.academy.bot.client.WebServiceProperties;
import backend.academy.bot.client.exception.ServiceUnavailableCircuitException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ScrapperTagClientImpl extends ScrapperClient implements ScrapperTagClient {

    private static final String TAG_PATH = "tag/{tgChatId}";
    private static final String ALL_ELEMENTS_PATH = "/all";

    public ScrapperTagClientImpl(WebClientProperties webClientProperties, WebServiceProperties webServiceProperties) {
        super(webClientProperties, webServiceProperties);
    }

    @CircuitBreaker(name = "ScrapperTagClient", fallbackMethod = "getListLinksByTagFallback")
    @Retry(name = "getListLinksByTag")
    @Override
    public ListLinksResponse getListLinksByTag(Long tgChatId, TagLinkRequest tagLinkRequest) {
        log.info("ScrapperClient getListLinksByTag {} ", tgChatId);

        return webClient
                .method(HttpMethod.GET)
                .uri(uriBuilder -> uriBuilder.path(TAG_PATH).build(tgChatId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(tagLinkRequest), TagLinkRequest.class)
                .retrieve()
                .onStatus(status -> status == HttpStatus.BAD_REQUEST, response -> response.bodyToMono(
                                ApiErrorResponse.class)
                        .map(error -> new ResponseException(error.description()))
                        .flatMap(Mono::error))
                .bodyToMono(ListLinksResponse.class)
                .timeout(wcp.globalTimeout())
                .block();
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private ListLinksResponse getListLinksByTagFallback(Long tgChatId, TagLinkRequest tagLinkRequest, Exception ex) {
        log.error("Circuit ДЕФОЛТ id {}, tagLinkRequest = {},  error: {}", tgChatId, tagLinkRequest, ex.getMessage());
        if (ex instanceof ResponseException) {
            throw new ResponseException(ex.getMessage());
        }
        throw new ServiceUnavailableCircuitException("Ошибка сервиса");
    }

    @CircuitBreaker(name = "ScrapperTagClient", fallbackMethod = "getAllListLinksByTagFallback")
    @Retry(name = "getAllListLinksByTag")
    @Override
    public TagListResponse getAllListLinksByTag(Long tgChatId) {
        return webClient
                .method(HttpMethod.GET)
                .uri(uriBuilder -> uriBuilder
                        .path(TAG_PATH + ALL_ELEMENTS_PATH) // Путь будет "tag/{tgChatId}/all"
                        .build(tgChatId))
                .retrieve()
                .onStatus(status -> status == HttpStatus.BAD_REQUEST, response -> response.bodyToMono(
                                ApiErrorResponse.class)
                        .map(error -> new ResponseException(error.description()))
                        .flatMap(Mono::error))
                .bodyToMono(TagListResponse.class)
                .timeout(wcp.globalTimeout())
                .block();
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private TagListResponse getAllListLinksByTagFallback(Long tgChatId, Exception ex) {
        log.error("Circuit ДЕФОЛТ id =  {}, ex = {}", tgChatId, ex.getMessage());
        if (ex instanceof ResponseException) {
            throw new ResponseException(ex.getMessage());
        }
        throw new ServiceUnavailableCircuitException("Ошибка сервиса");
    }

    @CircuitBreaker(name = "ScrapperTagClient", fallbackMethod = "removeTagFallback")
    @Retry(name = "removeTag")
    @Override
    public LinkResponse removeTag(Long tgChatId, TagRemoveRequest tg) {
        log.info("ScrapperClient untrackLink: tgChatId={}, request={}", tgChatId, tg);
        return webClient
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder.path(TAG_PATH).build(tgChatId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(tg), TagRemoveRequest.class)
                .retrieve()
                .onStatus(status -> status == HttpStatus.BAD_REQUEST, response -> response.bodyToMono(
                                ApiErrorResponse.class)
                        .map(error -> new ResponseException(error.description()))
                        .flatMap(Mono::error))
                .bodyToMono(LinkResponse.class)
                .timeout(wcp.globalTimeout())
                .block();
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private LinkResponse removeTagFallback(Long tgChatId, TagRemoveRequest tg, Exception ex) {
        log.error("Circuit ДЕФОЛТ id =  {}, tg = {}, ex = {}", tgChatId, tg, ex.getMessage());
        if (ex instanceof ResponseException) {
            throw new ResponseException(ex.getMessage());
        }
        throw new ServiceUnavailableCircuitException("Ошибка сервиса");
    }
}
