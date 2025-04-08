package backend.academy.scrapper.configuration;

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

    private final WebClient.Builder webClientBuilder;
    private final String baseUrl;

    public HttpTgBotClientConfig(
        WebClient.Builder webClientBuilder,
        @Value("${app.link.telegram-bot-uri}") String baseUrl) {
        this.webClientBuilder = webClientBuilder;
        this.baseUrl = baseUrl;
    }

    @Bean
    public TgBotClient createHttpTgBotClient() {
        WebClient webClient = webClientBuilder.baseUrl(baseUrl).build();
        return new HttpTgBotClient(webClient);
    }
}
