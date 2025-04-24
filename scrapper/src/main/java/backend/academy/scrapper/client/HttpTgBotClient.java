package backend.academy.scrapper.client;

import backend.academy.scrapper.configuration.api.WebClientProperties;
import backend.academy.scrapper.tracker.update.model.LinkUpdate;
import io.github.resilience4j.retry.annotation.Retry;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
public class HttpTgBotClient implements TgBotClient {

    private final WebClient webClient;
    public final WebClientProperties webClientProperties;

    public HttpTgBotClient(String baseUrl, WebClientProperties webClientProperties) {
        this.webClientProperties = webClientProperties;

        // Настраиваем таймауты через HttpClient
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(webClientProperties.responseTimeout()) // Таймаут на ответ
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int)
                        webClientProperties.connectTimeout().toMillis());

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Retry(name = "updatesPost", fallbackMethod = "fallback")
    @Override
    public void addUpdate(LinkUpdate linkUpdate) {
        log.info("обновления из TelegramBotClient {}", linkUpdate.url());
        webClient
                .post()
                .uri("/updates") // Убедитесь, что это правильный URI
                .contentType(MediaType.APPLICATION_JSON) // Указываем тип контента
                .body(Mono.just(linkUpdate), LinkUpdate.class) // Тело запроса
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.error("Ошибка клиента: {}", response.statusCode());
                    return response.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new RuntimeException("Ошибка клиента: " + errorBody)));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Ошибка сервера: {}", response.statusCode());
                    return Mono.error(new RuntimeException("Ошибка сервера: " + response.statusCode()));
                })
                .toBodilessEntity()
                .timeout(webClientProperties.globalTimeout())
                .doOnSuccess(response -> log.info("Обновление успешно отправлено: {}", linkUpdate.url()))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block(); // Блокируем выполнение для синхронного вызова
    }

    private void fallback(LinkUpdate linkUpdate, Exception ex) {
        log.error("Все попытки завершились ошибкой для {}", linkUpdate.url(), ex);
    }
}
