package backend.academy.bot.client.filter;

import backend.academy.bot.api.dto.request.filter.FilterRequest;
import backend.academy.bot.api.dto.response.ApiErrorResponse;
import backend.academy.bot.api.dto.response.filter.FilterListResponse;
import backend.academy.bot.api.dto.response.filter.FilterResponse;
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
public class ScrapperFilterClientImpl extends ScrapperClient implements ScrapperFilterClient {

    private static final String FILTER_PATH = "/filter/{tgChatId}";

    public ScrapperFilterClientImpl(WebClientProperties webClientProperties) {
        super(webClientProperties);
    }

    @Retry(name = "createFilter")
    @CircuitBreaker(name = "ScrapperFilterClient", fallbackMethod = "createFilterFallback")
    @Override
    public FilterResponse createFilter(Long chatId, FilterRequest filterRequest) {
        log.info("=========== ScrapperClient addFilter: tgChatId={}, filter={}", chatId, filterRequest.filter());
        return webClient
                .method(HttpMethod.POST)
                .uri(uriBuilder -> uriBuilder.path(FILTER_PATH).build(chatId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(filterRequest), FilterRequest.class)
                .retrieve()
                .onStatus(status -> status == HttpStatus.BAD_REQUEST, response -> response.bodyToMono(
                                ApiErrorResponse.class)
                        .map(error -> new ResponseException(error.description()))
                        .flatMap(Mono::error))
                .bodyToMono(FilterResponse.class)
                .timeout(wcp.globalTimeout())
                .block();
    }

    private FilterResponse createFilterFallback(Long chatId, FilterRequest filterRequest, Exception ex) {
        log.error(
                "Circuit ДЕФОЛТ {}. Error: {}",
                chatId,
                ex.getMessage() + "   " + ex.getClass().getName());
        if (ex instanceof ResponseException) {
            throw new ResponseException(ex.getMessage());
        }
        throw new ServiceUnavailableCircuitException("Сервис временно недоступен (Circuit Breaker)");
    }

    @Retry(name = "deleteFilter")
    @CircuitBreaker(name = "ScrapperFilterClient", fallbackMethod = "deleteFilterFallback")
    @Override
    public FilterResponse deleteFilter(Long tgChatId, FilterRequest filterRequest) {
        log.info("ScrapperClient deleteFilter: tgChatId={}, filter={}", tgChatId, filterRequest.filter());
        return webClient
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder.path(FILTER_PATH).build(tgChatId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(filterRequest)
                .retrieve()
                .onStatus(status -> status == HttpStatus.BAD_REQUEST, response -> response.bodyToMono(
                                ApiErrorResponse.class)
                        .map(error -> new ResponseException(error.description()))
                        .flatMap(Mono::error))
                .bodyToMono(FilterResponse.class)
                .timeout(wcp.globalTimeout())
                .block();
    }

    private FilterResponse deleteFilterFallback(Long tgChatId, FilterRequest filterRequest, Exception ex) {
        log.error(
                "Circuit ДЕФОЛТ {}. Error: {}",
                tgChatId,
                ex.getMessage() + "   " + ex.getClass().getName());
        if (ex instanceof ResponseException) {
            throw new ResponseException(ex.getMessage());
        }
        throw new ServiceUnavailableCircuitException("Сервис временно недоступен (Circuit Breaker)");
    }

    @Retry(name = "getFilterList")
    @CircuitBreaker(name = "ScrapperFilterClient", fallbackMethod = "getFilterListFallback")
    @Override
    public FilterListResponse getFilterList(Long id) {
        log.info("ScrapperClient getFilterList: tgChatId={}", id);
        return webClient
                .method(HttpMethod.GET)
                .uri(uriBuilder -> uriBuilder.path(FILTER_PATH).build(id))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status == HttpStatus.BAD_REQUEST, response -> response.bodyToMono(
                                ApiErrorResponse.class)
                        .map(error -> new ResponseException(error.description()))
                        .flatMap(Mono::error))
                .bodyToMono(FilterListResponse.class)
                .timeout(wcp.globalTimeout())
                .block();
    }

    private FilterListResponse getFilterListFallback(Long id, Exception ex) {
        log.error(
                "Circuit ДЕФОЛТ {}. Error: {}",
                id,
                ex.getMessage() + "   " + ex.getClass().getName());
        if (ex instanceof ResponseException) {
            throw new ResponseException(ex.getMessage());
        }
        throw new ServiceUnavailableCircuitException("Сервис временно недоступен (Circuit Breaker)");
    }
}
