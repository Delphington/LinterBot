package backend.academy.scrapper.tracker.client;

import backend.academy.scrapper.configuration.ScrapperConfig;
import backend.academy.scrapper.tracker.request.StackOverFlowRequest;
import backend.academy.scrapper.tracker.response.stack.AnswersResponse;
import backend.academy.scrapper.tracker.response.stack.CommentResponse;
import backend.academy.scrapper.tracker.response.stack.QuestionResponse;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.Optional;
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

    @Retry(name = "fetchQuestionStackOverFlow", fallbackMethod = "fetchQuestionFallback")
    public Optional<QuestionResponse> fetchQuestion(StackOverFlowRequest stackOverFlowRequest) {
        return Optional.ofNullable(webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/questions/{chatId}")
                        .queryParam("site", stackOverFlowRequest.site())
                        .queryParam("order", stackOverFlowRequest.order())
                        .queryParam("sort", stackOverFlowRequest.sort())
                        .build(stackOverFlowRequest.number()))
                .retrieve()
                .bodyToMono(QuestionResponse.class)
                .block());
    }

    @Retry(name = "fetchAnswerStackOverFlow", fallbackMethod = "fetchAnswerFallback")
    public Optional<AnswersResponse> fetchAnswer(StackOverFlowRequest stackOverFlowRequest) {
        return Optional.ofNullable(webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/questions/{chatId}/answers")
                        .queryParam("site", stackOverFlowRequest.site())
                        .queryParam("filter", stackOverFlowRequest.filter())
                        .build(stackOverFlowRequest.number()))
                .retrieve()
                .bodyToMono(AnswersResponse.class)
                .block());
    }

    @Retry(name = "fetchCommentStackOverFlow", fallbackMethod = "fetchCommentFallback")
    public Optional<CommentResponse> fetchComment(StackOverFlowRequest stackOverFlowRequest) {
        return Optional.ofNullable(webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/questions/{chatId}/comments")
                        .queryParam("site", stackOverFlowRequest.site())
                        .queryParam("filter", stackOverFlowRequest.filter())
                        .build(stackOverFlowRequest.number()))
                .retrieve()
                .bodyToMono(CommentResponse.class)
                .block());
    }

    private Optional<QuestionResponse> fetchQuestionFallback(StackOverFlowRequest stackOverFlowRequest, Exception ex) {
        log.error("Произошла ошибка fetchQuestionFall: {}", ex.getMessage());
        return Optional.empty();
    }

    private Optional<AnswersResponse> fetchAnswerFallback(StackOverFlowRequest stackOverFlowRequest, Exception ex) {
        log.error("Произошла ошибка fetchAnswer: {}", ex.getMessage());
        return Optional.empty();
    }

    private Optional<CommentResponse> fetchCommentFallback(StackOverFlowRequest stackOverFlowRequest, Exception ex) {
        log.error("Произошла ошибка fetchComment: {}", ex.getMessage());
        return Optional.empty();
    }
}
