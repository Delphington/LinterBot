package backend.academy.bot.client.tag;

import backend.academy.bot.api.dto.request.tag.TagLinkRequest;
import backend.academy.bot.api.dto.request.tag.TagRemoveRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.api.dto.response.TagListResponse;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.WebClientProperties;
import backend.academy.bot.client.exception.ServiceUnavailableCircuitException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ScrapperTagClientImpl extends ScrapperClient implements ScrapperTagClient {

    private static final String TAG_PATH = "tag/{tgChatId}";
    private static final String ALL_ELEMENTS_PATH = "/all";

    public ScrapperTagClientImpl(WebClientProperties webClientProperties) {
        super(webClientProperties);
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
                .bodyToMono(ListLinksResponse.class)
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
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
                .bodyToMono(TagListResponse.class)
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
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
                .bodyToMono(LinkResponse.class)
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    // Fallback методы
    private ListLinksResponse getListLinksByTagFallback(Long tgChatId, TagLinkRequest tagLinkRequest, Exception ex) {
        log.warn(
                "Fallback triggered for getListLinksByTag (chat: {}, tag: {}). Error: {}",
                tgChatId,
                tagLinkRequest.tag(),
                ex.getMessage());
        throw new ServiceUnavailableCircuitException("Tagged links retrieval service unavailable. Please try later");
    }

    private TagListResponse getAllListLinksByTagFallback(Long tgChatId, Exception ex) {
        log.warn("Fallback triggered for getAllListLinksByTag (chat: {}). Error: {}", tgChatId, ex.getMessage());
        throw new ServiceUnavailableCircuitException(
                "All tagged links retrieval service unavailable. Please try later");
    }

    private LinkResponse removeTagFallback(Long tgChatId, TagRemoveRequest tg, Exception ex) {
        log.warn("Fallback triggered for removeTag (chat: {}). Error: {}", tgChatId, ex.getMessage());
        throw new ServiceUnavailableCircuitException("Tag removal service unavailable. Please try later");
    }
}
