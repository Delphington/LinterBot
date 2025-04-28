package backend.academy.bot.client.chat;

import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.WebClientProperties;
import backend.academy.bot.client.exception.ServiceUnavailableCircuitException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
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
    @CircuitBreaker(name = "ScrapperTagClient", fallbackMethod = "registerChatFallback")
    @Override
    public void registerChat(final Long tgChatId) {
        log.info("ScrapperClient registerChat!!!! {} ", tgChatId);
        webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(TG_CHAT_PATH).build(tgChatId))
                .retrieve()
                .bodyToMono(Void.class)
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    @CircuitBreaker(name = "ScrapperTgChatClient", fallbackMethod = "deleteChatFallback")
    @Retry(name = "deleteChat")
    @Override
    public LinkResponse deleteChat(final Long tgChatId, final RemoveLinkRequest request) {
        log.info("ScrapperClient deleteLink {} ", tgChatId);
        return webClient
                .method(HttpMethod.DELETE)
                .uri(TG_CHAT_PATH, tgChatId)
                .body(Mono.just(request), RemoveLinkRequest.class)
                .retrieve()
                .bodyToMono(LinkResponse.class)
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    private void registerChatFallback(Long tgChatId, Exception ex) {
        log.warn("Fallback triggered for registerChat (chatId: {}). Error: {}", tgChatId, ex.getMessage());
        throw new ServiceUnavailableCircuitException("Chat registration service unavailable. Please try later");
    }

    private LinkResponse deleteChatFallback(Long tgChatId, RemoveLinkRequest request, Exception ex) {
        log.warn("Fallback triggered for deleteChat (chatId: {}). Error: {}", tgChatId, ex.getMessage());
        throw new ServiceUnavailableCircuitException("Chat deletion service unavailable. Please try later");
    }
}
