package backend.academy.scrapper.tracker.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Collections;
import java.util.List;

@Slf4j
public abstract class BaseWebClient {
    protected final WebClient webClient;

    protected BaseWebClient(WebClient.Builder webClientBuilder, String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }
//
//    protected <T> T get(String uri, Class<T> responseType, Object... uriVariables) {
//        return webClient
//            .get()
//            .uri(uri, uriVariables)
//            .retrieve()
//            .bodyToMono(responseType)
//            .block();
//    }
//
//    protected <T> List<T> getList(String uri, Class<T> responseType, Object... uriVariables) {
//        return webClient
//            .get()
//            .uri(uri, uriVariables)
//            .retrieve()
//            .bodyToFlux(responseType)
//            .collectList()
//            .blockOptional()
//            .orElse(Collections.emptyList());
//    }

}
