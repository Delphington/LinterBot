package backend.academy.bot.client.link;

import backend.academy.bot.api.dto.request.AddLinkRequest;
import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
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
public class ScrapperLinkClientImpl extends ScrapperClient implements ScrapperLinkClient {

    private static final String LINK_PATH = "links/{tgChatId}";

    public ScrapperLinkClientImpl(WebClientProperties webClientProperties) {
        super(webClientProperties);
    }

    @CircuitBreaker(name = "ScrapperLinkClient", fallbackMethod = "trackLinkFallback")
    @Retry(name = "trackLink")
    @Override
    public LinkResponse trackLink(final Long tgChatId, final AddLinkRequest request) {
        log.info("ScrapperClient trackLink {} ", tgChatId);

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(LINK_PATH).build(tgChatId))
                .header("Tg-Chat-Id", String.valueOf(tgChatId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), AddLinkRequest.class)
                .retrieve()
                .bodyToMono(LinkResponse.class)
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    @CircuitBreaker(name = "ScrapperLinkClient", fallbackMethod = "untrackLinkFallback")
    @Retry(name = "untrackLink")
    @Override
    public LinkResponse untrackLink(final Long tgChatId, final RemoveLinkRequest request) {
        log.info("ScrapperClient untrackLink {} ", tgChatId);

        return webClient
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder.path(LINK_PATH).build(tgChatId))
                .header("Tg-Chat-Id", String.valueOf(tgChatId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), RemoveLinkRequest.class)
                .retrieve()
                .bodyToMono(LinkResponse.class)
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    @CircuitBreaker(name = "ScrapperLinkClient", fallbackMethod = "getListLinkFallback")
    @Retry(name = "untrackLink")
    @Override
    public ListLinksResponse getListLink(final Long tgChatId) {
        log.info("ScrapperClient getListLink {} ", tgChatId);

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("links").build())
                .header("Tg-Chat-Id", String.valueOf(tgChatId))
                .retrieve()
                .bodyToMono(ListLinksResponse.class)
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    // Fallback методы
    private LinkResponse trackLinkFallback(Long tgChatId, AddLinkRequest request, Exception ex) {
        log.warn(
                "Fallback triggered for trackLink (chat: {}, url: {}). Error: {}",
                tgChatId,
                request.link(),
                ex.getMessage());
        throw new ServiceUnavailableCircuitException("Link tracking service unavailable. Please try later");
    }

    private LinkResponse untrackLinkFallback(Long tgChatId, RemoveLinkRequest request, Exception ex) {
        log.warn("Fallback triggered for untrackLink (chat: {}). Error: {}", tgChatId, ex.getMessage());
        throw new ServiceUnavailableCircuitException("Link untracking service unavailable. Please try later");
    }

    private ListLinksResponse getListLinkFallback(Long tgChatId, Exception ex) {
        log.warn("Fallback triggered for getListLink (chat: {}). Error: {}", tgChatId, ex.getMessage());
        throw new ServiceUnavailableCircuitException("Links list retrieval service unavailable. Please try later");
    }
}
