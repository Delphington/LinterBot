package backend.academy.scrapper.tracker.client;

import backend.academy.scrapper.tracker.update.model.LinkUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class TelegramBotClient {

    private final WebClient webClient;

    public TelegramBotClient(
            final WebClient.Builder webClientBuilder,
            @Value("${app.link.telegram-bot-uri}") String baseUrl
    ) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }


    public void addUpdate(LinkUpdate linkUpdate) {
        log.info("Мы хотим отправить обновления из TelegramBotClient {}", linkUpdate.url());
        webClient.post()
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
                .doOnSuccess(response -> log.info("Обновление успешно отправлено: {}", linkUpdate.url()))
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
                .block(); // Блокируем выполнение для синхронного вызова
    }

    //public void addUpdate(LinkUpdatesRequest linkRequest) {
//        restClient.post()
//            .uri("/updates")
//            .contentType(APPLICATION_JSON)
//            .body(linkRequest)
//            .retrieve()
//            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
//                throw new ResponseException(response.getStatusCode().toString());
//            })
//            .toBodilessEntity();
//    }

}
