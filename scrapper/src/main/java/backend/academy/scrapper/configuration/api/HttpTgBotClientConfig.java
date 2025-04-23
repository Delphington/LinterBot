package backend.academy.scrapper.configuration.api;

import backend.academy.scrapper.client.HttpTgBotClient;
import backend.academy.scrapper.client.TgBotClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "message-transport", havingValue = "HTTP")
public class HttpTgBotClientConfig {

    private final String baseUrl;
    private final WebClientProperties webClientProperties;

    public HttpTgBotClientConfig(
            @Value("${app.link.telegram-bot-uri}") String baseUrl,
            WebClientProperties webClientProperties) {
        this.baseUrl = baseUrl;
        this.webClientProperties = webClientProperties;
    }

    @Bean
    public TgBotClient createHttpTgBotClient() {
        return new HttpTgBotClient(baseUrl, webClientProperties);
    }
}
