package backend.academy.bot.client;

import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Slf4j
public abstract class ScrapperClient {

    protected final WebClient webClient;
    protected final WebClientProperties wcp;

    public ScrapperClient(WebClientProperties webClientProperties, WebServiceProperties webServiceProperties) {
        this.wcp = webClientProperties;
        // Настраиваем таймауты через HttpClient
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(webClientProperties.responseTimeout()) // Таймаут на ответ
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int)
                        webClientProperties.connectTimeout().toMillis());

        log.error("BASE url: {}", webServiceProperties.scrapperUri());
        log.error(
                "Propertises connection: {}, global {}",
                webClientProperties.connectTimeout(),
                webClientProperties.globalTimeout());

        this.webClient = WebClient.builder()
                .baseUrl(webServiceProperties.scrapperUri())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
