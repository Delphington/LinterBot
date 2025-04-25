package backend.academy.bot.client.link;

import backend.academy.bot.api.dto.request.AddLinkRequest;
import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
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
public class ScrapperLinkClientImpl extends ScrapperClient implements ScrapperLinkClient {

    private static final String LINK_PATH = "links/{tgChatId}";

    public ScrapperLinkClientImpl(WebClientProperties webClientProperties) {
        super(webClientProperties);
    }

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
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        ErrorResponseHandler.handleClientError("Ошибка добавление ссылки"))
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        ErrorResponseHandler.handleServerError("Ошибка добавление ссылки"))
                .bodyToMono(LinkResponse.class)
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

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
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        ErrorResponseHandler.handleClientError("Ошибка уд списка ссылок"))
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        ErrorResponseHandler.handleServerError("Ошибка получении списка ссылок"))
                .bodyToMono(LinkResponse.class)
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }

    @Retry(name = "untrackLink")
    @Override
    public ListLinksResponse getListLink(final Long tgChatId) {
        log.info("ScrapperClient getListLink {} ", tgChatId);

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("links").build())
                .header("Tg-Chat-Id", String.valueOf(tgChatId))
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        ErrorResponseHandler.handleClientError("Ошибка получении списка ссылок"))
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        ErrorResponseHandler.handleServerError("Ошибка получении списка ссылок"))
                .bodyToMono(ListLinksResponse.class)
                .timeout(wcp.globalTimeout())
                .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block();
    }
}
