package backend.academy.scrapper.client.type;

import backend.academy.scrapper.configuration.api.WebClientProperties;
import backend.academy.scrapper.tracker.update.model.LinkUpdate;
import io.github.resilience4j.retry.annotation.Retry;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Component
@Slf4j
public class HttpUpdateSender implements UpdateSender {

    private final WebClient webClient;
    private final WebClientProperties webClientProperties;

    public HttpUpdateSender(
            @Value("${app.link.telegram-bot-uri}") String baseUrl, WebClientProperties webClientProperties) {
        this.webClientProperties = webClientProperties;
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(webClientProperties.responseTimeout())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int)
                        webClientProperties.connectTimeout().toMillis());

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Retry(name = "httpSendUpdate", fallbackMethod = "sendUpdateFallback")
    @Override
    public void sendUpdate(LinkUpdate linkUpdate) {
        log.info("Отправка обновления: {}", linkUpdate.url());
        webClient
                .post()
                .uri("/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(linkUpdate)
                .retrieve()
                .toBodilessEntity()
                .timeout(webClientProperties.globalTimeout())
                .block();
    }

    public void sendUpdateFallback(LinkUpdate linkUpdate, Exception ex) {
        log.error("HttpUpdateSender не работает HTTP: ");
        throw new RuntimeException("HTTP не работает");
    }
}
