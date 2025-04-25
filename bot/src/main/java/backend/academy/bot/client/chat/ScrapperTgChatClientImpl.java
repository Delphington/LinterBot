package backend.academy.bot.client.chat;

import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.client.ErrorResponseHandler;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.WebClientProperties;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
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
    @Override
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

    @Retry(name = "deleteChat")
    public LinkResponse deleteChat(final Long tgChatId, final RemoveLinkRequest request) {
        log.info("ScrapperClient deleteLink {} ", tgChatId);
        return webClient
                .method(HttpMethod.DELETE)
                .uri(TG_CHAT_PATH, tgChatId)
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
}
