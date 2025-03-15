package backend.academy.scrapper.tracker.client;

import org.springframework.web.reactive.function.client.WebClient;

public abstract class BaseWebClient {
    protected final WebClient webClient;

    protected BaseWebClient(WebClient.Builder webClientBuilder, String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }
}
