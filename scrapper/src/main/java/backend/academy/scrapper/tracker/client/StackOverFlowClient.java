package backend.academy.scrapper.tracker.client;

import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.request.StackOverFlowRequest;
import backend.academy.scrapper.response.StackOverFlowResponse;
import org.springframework.web.reactive.function.client.WebClient;

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
