package backend.academy.scrapper.tracker.client;

import backend.academy.scrapper.configuration.ScrapperConfig;
import backend.academy.scrapper.tracker.request.StackOverFlowRequest;
import backend.academy.scrapper.tracker.response.stack.AnswersResponse;
import backend.academy.scrapper.tracker.response.stack.CommentResponse;
import backend.academy.scrapper.tracker.response.stack.QuestionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public class StackOverFlowClient extends BaseWebClient {

    public StackOverFlowClient(ScrapperConfig.StackOverflowCredentials stackOverflowCredentials) {
        super(WebClient.builder(), stackOverflowCredentials.stackOverFlowUrl());
        if (stackOverflowCredentials.key() != null
                && !stackOverflowCredentials.key().isEmpty()) {
            webClient.mutate().defaultHeader("key", stackOverflowCredentials.key());
        }
        if (stackOverflowCredentials.accessToken() != null
                && !stackOverflowCredentials.accessToken().isEmpty()) {
            webClient.mutate().defaultHeader("access_token", stackOverflowCredentials.accessToken());
        }
    }

    public QuestionResponse fetchQuestion(StackOverFlowRequest stackOverFlowRequest) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/questions/{id}")
                        .queryParam("site", stackOverFlowRequest.site())
                        .queryParam("order", stackOverFlowRequest.order())
                        .queryParam("sort", stackOverFlowRequest.sort())
                        .build(stackOverFlowRequest.number()))
                .retrieve()
                .bodyToMono(QuestionResponse.class)
                .block();
    }

    public AnswersResponse fetchAnswer(StackOverFlowRequest stackOverFlowRequest) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/questions/{id}/answers")
                        .queryParam("site", stackOverFlowRequest.site())
                        .queryParam("filter", stackOverFlowRequest.filter())
                        .build(stackOverFlowRequest.number()))
                .retrieve()
                .bodyToMono(AnswersResponse.class)
                .block();
    }

    public CommentResponse fetchComment(StackOverFlowRequest stackOverFlowRequest) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/questions/{id}/comments")
                        .queryParam("site", stackOverFlowRequest.site())
                        .queryParam("filter", stackOverFlowRequest.filter())
                        .build(stackOverFlowRequest.number()))
                .retrieve()
                .bodyToMono(CommentResponse.class)
                .block();
    }
}
