package backend.academy.scrapper.client;

import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.request.GitHubRequest;
import backend.academy.scrapper.request.StackOverFlowRequest;
import backend.academy.scrapper.response.GitHubResponse;
import backend.academy.scrapper.response.StackOverFlowResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.OffsetDateTime;

public class StackOverFlowClient {
    private final ScrapperConfig.StackOverflowCredentials stackOverflowCredentials;
    private WebClient webClient;

    public StackOverFlowClient(ScrapperConfig.StackOverflowCredentials stackOverflowCredentials) {
        this.stackOverflowCredentials = stackOverflowCredentials;
        this.webClient = WebClient.builder().baseUrl(stackOverflowCredentials.stackOverFlowUrl()).build();
    }


    public StackOverFlowResponse getFetchDate(StackOverFlowRequest request) {
        var client = this.webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(String.format("%s", request.number()))
                .queryParam("order", request.order())
                .queryParam("sort", request.sort())
                .queryParam("site", request.site())
                //  .queryParam("filter", request.filter())
                .build())
            .retrieve().bodyToMono(StackOverFlowResponse.class)
            .block();

        return client;
    }
}
