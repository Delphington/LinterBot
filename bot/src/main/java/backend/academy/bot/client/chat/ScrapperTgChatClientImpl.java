package backend.academy.bot.client.chat;

import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.ApiErrorResponse;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.WebClientProperties;
import backend.academy.bot.client.exception.ServiceUnavailableCircuitException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ScrapperTgChatClientImpl extends ScrapperClient implements ScrapperTgChatClient {

    private static final String TG_CHAT_PATH = "tg-chat/{chatId}";

    public ScrapperTgChatClientImpl(WebClientProperties webClientProperties) {
        super(webClientProperties);
    }

    @Retry(name = "registerChat")
    @CircuitBreaker(name = "ScrapperChatClient", fallbackMethod = "registerChatFallback")
    @Override
    public void registerChat(Long tgChatId) {
        log.info("ScrapperClient registerChat!!!! {} ", tgChatId);
        webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(TG_CHAT_PATH).build(tgChatId))
                .retrieve()
                .onStatus(status -> status == HttpStatus.BAD_REQUEST, response -> response.bodyToMono(
                                ApiErrorResponse.class)
                        .map(error -> new ResponseException(error.description()))
                        .flatMap(Mono::error))
                .bodyToMono(Void.class)
                .timeout(wcp.globalTimeout())
                .block();
    }

    private void registerChatFallback(Long tgChatId, Exception ex) {
        log.error(
                "Circuit ДЕФОЛТ {}. Error: {}",
                tgChatId,
                ex.getMessage() + "   " + ex.getClass().getName());
        if (ex instanceof ResponseException) {
            throw new ResponseException(ex.getMessage());
        }
        throw new ServiceUnavailableCircuitException("Ошибка сервиса");
    }

    @Retry(name = "deleteChat")
    @CircuitBreaker(name = "ScrapperChatClient", fallbackMethod = "deleteChatFallback")
    @Override
    public LinkResponse deleteChat(Long tgChatId, RemoveLinkRequest request) {
        log.info("ScrapperClient deleteLink {} ", tgChatId);
        return webClient
                .method(HttpMethod.DELETE)
                .uri(TG_CHAT_PATH, tgChatId)
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

    private void deleteChatFallback(Long tgChatId, RemoveLinkRequest request, Exception ex) {
        log.error(
                "Circuit ДЕФОЛТ {}. Error: {}",
                tgChatId,
                ex.getMessage() + "   " + ex.getClass().getName());
        if (ex instanceof ResponseException) {
            throw new ResponseException(ex.getMessage());
        }
        throw new ServiceUnavailableCircuitException("Ошибка сервиса");
    }
}
