package backend.academy.scrapper.tracker.client;

import backend.academy.scrapper.configuration.api.WebClientProperties;
import io.netty.channel.ChannelOption;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

public abstract class BaseWebClient {
    protected final WebClient webClient;
    protected final WebClientProperties webClientProperties;

    protected BaseWebClient(String baseUrl, WebClientProperties webClientProperties) {
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
}
