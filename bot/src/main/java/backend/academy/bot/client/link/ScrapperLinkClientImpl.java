package backend.academy.bot.client.link;

import backend.academy.bot.api.dto.request.AddLinkRequest;
import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.ApiErrorResponse;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.WebClientProperties;
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
public class ScrapperLinkClientImpl extends ScrapperClient implements ScrapperLinkClient {

    private static final String LINK_PATH = "links/{tgChatId}";

    public ScrapperLinkClientImpl(WebClientProperties webClientProperties) {
        super(webClientProperties);
    }

    @CircuitBreaker(name = "ScrapperLinkClient", fallbackMethod = "trackLinkFallback")
    @Retry(name = "trackLink")
    @Override
    public LinkResponse trackLink(Long tgChatId, AddLinkRequest request) {
        log.info("ScrapperClient trackLink {} ", tgChatId);

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(LINK_PATH).build(tgChatId))
                .header("Tg-Chat-Id", String.valueOf(tgChatId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), AddLinkRequest.class)
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
    private LinkResponse trackLinkFallback(Long tgChatId, AddLinkRequest request, Exception ex) {
        log.error("Circuit ДЕФОЛТ id  = {}, request = {} Error: {}", tgChatId, request, ex.getMessage());

        if (ex instanceof ResponseException) {
            throw new ResponseException(ex.getMessage());
        }
        throw new ServiceUnavailableCircuitException("Сервис временно недоступен (Circuit Breaker)");
    }

    @CircuitBreaker(name = "ScrapperLinkClient", fallbackMethod = "untrackLinkFallback")
    @Retry(name = "untrackLink")
    @Override
    public LinkResponse untrackLink(Long tgChatId, RemoveLinkRequest request) {
        log.info("ScrapperClient untrackLink {} ", tgChatId);

        return webClient
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder.path(LINK_PATH).build(tgChatId))
                .header("Tg-Chat-Id", String.valueOf(tgChatId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), RemoveLinkRequest.class)
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
    private LinkResponse untrackLinkFallback(Long tgChatId, RemoveLinkRequest request, Exception ex) {
        log.error("Circuit ДЕФОЛТ id  = {}, request = {}, Error: {}", tgChatId, request, ex.getMessage());
        if (ex instanceof ResponseException) {
            throw new ResponseException(ex.getMessage());
        }
        throw new ServiceUnavailableCircuitException("Сервис временно недоступен (Circuit Breaker)");
    }

    @CircuitBreaker(name = "ScrapperLinkClient", fallbackMethod = "getListLinkFallback")
    @Retry(name = "untrackLink")
    @Override
    public ListLinksResponse getListLink(Long tgChatId) {
        log.info("ScrapperClient getListLink {} ", tgChatId);

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("links").build())
                .header("Tg-Chat-Id", String.valueOf(tgChatId))
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
    private ListLinksResponse getListLinkFallback(Long tgChatId, Exception ex) {
        log.error("Circuit ДЕФОЛТ id  = {}, Error: {}", tgChatId, ex.getMessage());

        if (ex instanceof ResponseException) {
            throw new ResponseException(ex.getMessage());
        }
        throw new ServiceUnavailableCircuitException("Сервис временно недоступен (Circuit Breaker)");
    }
}
