package backend.academy.bot.client.filter;

import backend.academy.bot.api.dto.request.filter.FilterRequest;
import backend.academy.bot.api.dto.response.filter.FilterListResponse;
import backend.academy.bot.api.dto.response.filter.FilterResponse;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.WebClientProperties;
import backend.academy.bot.client.exception.ServiceUnavailableCircuitException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ScrapperFilterClientImpl extends ScrapperClient implements ScrapperFilterClient {

    private static final String FILTER_PATH = "/filter/{tgChatId}";

    public ScrapperFilterClientImpl(
            WebClientProperties webClientProperties, CircuitBreakerRegistry circuitBreakerRegistry) {
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
                .bodyToMono(FilterResponse.class)
                .timeout(wcp.globalTimeout())
                .block();
    }

    @CircuitBreaker(name = "ScrapperFilterClient", fallbackMethod = "deleteFilterFallback")
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
                .bodyToMono(FilterResponse.class)
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    @CircuitBreaker(name = "ScrapperFilterClient", fallbackMethod = "getFilterListFallback")
    @Retry(name = "getFilterList")
    @Override
    public FilterListResponse getFilterList(Long id) {
        log.info("ScrapperClient getFilterList: tgChatId={}", id);
        return webClient
                .method(HttpMethod.GET)
                .uri(uriBuilder -> uriBuilder.path(FILTER_PATH).build(id))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(FilterListResponse.class)
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    // Fallback методы для каждого endpoint
    private FilterResponse createFilterFallback(Long chatId, FilterRequest filterRequest, Exception ex) {
        log.warn(
                "Fallback triggered for createFilter (chatId: {}, filter: {}). Error: {}",
                chatId,
                filterRequest.filter(),
                ex.getMessage());
        throw new ServiceUnavailableCircuitException("Filter creation service unavailable. Please try later");
    }

    private FilterResponse deleteFilterFallback(Long chatId, FilterRequest filterRequest, Exception ex) {
        log.warn(
                "Fallback triggered for deleteFilter (chatId: {}, filter: {}). Error: {}",
                chatId,
                filterRequest.filter(),
                ex.getMessage());
        throw new ServiceUnavailableCircuitException("Filter deletion service unavailable. Please try later");
    }

    private FilterListResponse getFilterListFallback(Long chatId, Exception ex) {
        log.warn("Fallback triggered for getFilterList (chatId: {}). Error: {}", chatId, ex.getMessage());
        throw new ServiceUnavailableCircuitException("Filter list service unavailable. Please try later");
    }
}
