package backend.academy.bot.client;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

public abstract class ScrapperClient {

    protected final WebClient webClient;
    protected final WebClientProperties wcp;

  //  @Value("${app.link.scrapper-uri}")
    private String baseUrl = "http://localhost:8081";

    public ScrapperClient(WebClientProperties webClientProperties) {
        this.wcp = webClientProperties;

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
