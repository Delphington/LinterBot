package backend.academy.scrapper.tracker.update;

import backend.academy.scrapper.client.TgBotClient;
import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.exception.link.LinkNotFoundException;
import backend.academy.scrapper.repository.TgChatLinkRepository;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.tracker.client.GitHubClient;
import backend.academy.scrapper.tracker.client.StackOverFlowClient;
import backend.academy.scrapper.tracker.request.GitHubRequest;
import backend.academy.scrapper.tracker.request.StackOverFlowRequest;
import backend.academy.scrapper.tracker.response.github.GitHubResponse;
import backend.academy.scrapper.tracker.response.github.IssueResponse;
import backend.academy.scrapper.tracker.response.github.PullRequestResponse;
import backend.academy.scrapper.tracker.response.stack.AnswersResponse;
import backend.academy.scrapper.tracker.response.stack.CommentResponse;
import backend.academy.scrapper.tracker.response.stack.QuestionResponse;
import backend.academy.scrapper.tracker.update.dto.LinkDto;
import backend.academy.scrapper.tracker.update.exception.BadLinkRequestException;
import backend.academy.scrapper.tracker.update.model.LinkUpdate;
import backend.academy.scrapper.util.Utils;
import io.micrometer.core.instrument.Timer;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Getter
@Slf4j
@RequiredArgsConstructor
@Component
public class LinkUpdateProcessor implements Constance {

    private final TgBotClient tgBotClient;

    private final GitHubClient gitHubClient;
    private final StackOverFlowClient stackOverFlowClient;
    private final LinkService linkService;
    private final TgChatLinkRepository tgChatLinkRepository;

    private List<LinkDto> updatedLinkList = new ArrayList<>();

    private static final String CONST_GITHUB = "github";
    private static final String CONST_STACK_OVER_FLOW = "stackoverflow";

    // Для активных ссылок
    private final AtomicInteger githubProcessedLinksCounter;
    private final AtomicInteger stackoverflowProcessedLinksCounter;

    // Перцентиль
    private final Timer githubScrapeTimer;
    private final Timer stackoverflowScrapeTimer;

    public void updateLink(List<LinkDto> linkList) {
        updatedLinkList = new ArrayList<>();
        for (LinkDto item : linkList) {
            String urlString = item.url().toString();

            if (urlString.contains(CONST_GITHUB)) {
                githubProcessedLinksCounter.incrementAndGet();
                handlerUpdateGitHub(item);
            } else if (urlString.contains(CONST_STACK_OVER_FLOW)) {
                stackoverflowProcessedLinksCounter.incrementAndGet();
                handlerUpdateStackOverFlow(item);
            } else {
                throw new BadLinkRequestException(
                        "Ссылка не может быть обработана, " + "так как это не github и не stackoverflow");
            }
        }
        for (LinkDto item : updatedLinkList) {
            List<Long> chatIds = tgChatLinkRepository.findChatIdsByLinkId(item.id());
            tgBotClient.sendUpdate(new LinkUpdate(item.id(), item.url(), item.descriptionUpdate(), chatIds));
        }
    }

    public void handlerUpdateGitHub(LinkDto linkDto) {

        githubScrapeTimer.record(() -> {
            if (linkDto.lastUpdated() == null) {
                linkDto.lastUpdated(OffsetDateTime.now(ZoneId.systemDefault()));
                Link link = linkService
                        .findById(linkDto.id())
                        .orElseThrow(() -> new LinkNotFoundException("Ссылка с ID " + linkDto.id() + " не найдена"));
                link.updatedAt(OffsetDateTime.now(ZoneId.systemDefault()));
                linkService.update(link);

                return;
            }

            GitHubRequest gitHubRequest =
                    Utils.parseUrlToGithubRequest(linkDto.url().toString());

            Optional<List<IssueResponse>> issuesListOptional =
                    gitHubClient.fetchIssue(gitHubRequest, linkDto.lastUpdated());
            Optional<List<PullRequestResponse>> pullRequestListOptional =
                    gitHubClient.fetchPullRequest(gitHubRequest, linkDto.lastUpdated());

            Optional<GitHubResponse> gitHubResponseOptional = gitHubClient.getFetchDate(gitHubRequest);

            StringBuilder issueStringBuilder = new StringBuilder();
            StringBuilder pullRequestStringBuilder = new StringBuilder();
            StringBuilder repositoryStringBuilder = new StringBuilder();

            if (issuesListOptional.isPresent()) {
                List<IssueResponse> issuesListTemp =
                        issuesListOptional.orElseThrow(() -> new IllegalStateException("Optional is Empty"));
                issueStringBuilder = updateFetchIssue(linkDto, issuesListTemp);
            }

            if (pullRequestListOptional.isPresent()) {
                List<PullRequestResponse> pullRequestListTemp =
                        pullRequestListOptional.orElseThrow(() -> new IllegalStateException("Optional is Empty"));
                pullRequestStringBuilder = updateFetchPullRequest(linkDto, pullRequestListTemp);
            }

            if (gitHubResponseOptional.isPresent()) {
                GitHubResponse gitHubResponseTemp =
                        gitHubResponseOptional.orElseThrow(() -> new IllegalStateException("Optional is Empty"));
                repositoryStringBuilder = updateFetchRepository(linkDto, gitHubResponseTemp);
            }

            if (!issueStringBuilder.isEmpty()
                    || !pullRequestStringBuilder.isEmpty()
                    || !repositoryStringBuilder.isEmpty()) {
                linkDto.lastUpdated(OffsetDateTime.now(ZoneId.systemDefault()));

                Link link = linkService
                        .findById(linkDto.id())
                        .orElseThrow(() -> new LinkNotFoundException("ID " + linkDto.id() + "ссылка не найдена"));
                link.updatedAt(OffsetDateTime.now(ZoneId.systemDefault()));
                linkService.update(link);

                StringBuilder temp = new StringBuilder();
                temp.append(CONST_SPACE)
                        .append(CONST_NEXT_LINE)
                        .append(CONST_SYMBOL)
                        .append(" Репозиторий: ");
                gitHubResponseOptional.ifPresent(gitHubResponse -> temp.append(gitHubResponse.repositoryName()));
                temp.append(CONST_NEXT_LINE)
                        .append(pullRequestStringBuilder)
                        .append(CONST_NEXT_LINE)
                        .append(issueStringBuilder)
                        .append(CONST_NEXT_LINE)
                        .append(repositoryStringBuilder)
                        .append(CONST_NEXT_LINE);

                linkDto.descriptionUpdate(temp.toString());
                updatedLinkList.add(linkDto);
            }
        });
    }

    public StringBuilder updateFetchRepository(LinkDto linkDto, GitHubResponse gitHubResponse) {
        StringBuilder temp = new StringBuilder();
        if (gitHubResponse.updatedAt() != null && linkDto.lastUpdated().isBefore(gitHubResponse.updatedAt())) {
            temp.append(CONST_SYMBOL).append(" Обновление: Произошло изменения репозитория!\n");
        }
        return temp;
    }

    public StringBuilder updateFetchPullRequest(LinkDto linkDto, List<PullRequestResponse> pullRequestResponseList) {
        StringBuilder temp = new StringBuilder();
        for (PullRequestResponse item : pullRequestResponseList) {
            if (linkDto.lastUpdated().isBefore(item.updatedAt())) {
                temp.append(CONST_SYMBOL).append(CONST_PULL_REQUEST);
                temp.append(CONST_SYMBOL)
                        .append(CONST_TITLE)
                        .append(item.title())
                        .append(CONST_NEXT_LINE);
                temp.append(CONST_SYMBOL)
                        .append(CONST_USER)
                        .append(item.user().login())
                        .append(CONST_NEXT_LINE);
                temp.append(CONST_SYMBOL)
                        .append(CONST_COMMENT)
                        .append(item.updatedAt())
                        .append(CONST_NEXT_LINE);
                temp.append(CONST_SYMBOL)
                        .append(CONST_DESCRIPTION)
                        .append(item.text())
                        .append(CONST_NEXT_LINE);
            }
        }
        return temp;
    }

    public StringBuilder updateFetchIssue(LinkDto linkDto, List<IssueResponse> issuesList) {
        StringBuilder temp = new StringBuilder();
        for (IssueResponse item : issuesList) {
            if (linkDto.lastUpdated().isBefore(item.updatedAt())) {
                temp.append(CONST_SYMBOL).append(CONST_ISSUE);
                temp.append(CONST_SYMBOL)
                        .append(CONST_TITLE)
                        .append(item.title())
                        .append(CONST_NEXT_LINE);
                temp.append(CONST_SYMBOL)
                        .append(CONST_USER)
                        .append(item.user().login())
                        .append(CONST_NEXT_LINE);
                temp.append(CONST_SYMBOL)
                        .append(CONST_CREATED_AT)
                        .append(item.updatedAt())
                        .append(CONST_NEXT_LINE);
                temp.append(CONST_SYMBOL)
                        .append(CONST_DESCRIPTION)
                        .append(item.text())
                        .append(CONST_NEXT_LINE);
            }
        }
        return temp;
    }

    // Вопрос: https://api.stackexchange.com/2.3/questions/79486408?order=desc&sort=activity&site=stackoverflow
    // Коммент https://api.stackexchange.com/2.3/questions/79486408/comments?site=stackoverflow&filter=withbody

    public void handlerUpdateStackOverFlow(LinkDto linkDto) {
        stackoverflowScrapeTimer.record(() -> {
            if (linkDto.lastUpdated() == null) {
                linkDto.lastUpdated(OffsetDateTime.now(ZoneId.systemDefault()));
                Link link = linkService
                        .findById(linkDto.id())
                        .orElseThrow(() -> new LinkNotFoundException("Ссылка с ID " + linkDto.id() + " не найдена"));
                link.updatedAt(OffsetDateTime.now(ZoneId.systemDefault()));
                linkService.update(link);
                return;
            }

            StackOverFlowRequest stackOverFlowRequest =
                    Utils.parseUrlToStackOverFlowRequest(linkDto.url().toString());

            Optional<QuestionResponse> questionResponseOptional =
                    stackOverFlowClient.fetchQuestion(stackOverFlowRequest);
            Optional<CommentResponse> commentResponseOptional = stackOverFlowClient.fetchComment(stackOverFlowRequest);
            Optional<AnswersResponse> answersResponseOptional = stackOverFlowClient.fetchAnswer(stackOverFlowRequest);

            StringBuilder answerStringBuilder = new StringBuilder();
            StringBuilder commentStringBuilder = new StringBuilder();
            StringBuilder questionStringBuilder = new StringBuilder();

            if (questionResponseOptional.isPresent()) {
                QuestionResponse questionResponseTemp =
                        questionResponseOptional.orElseThrow(() -> new IllegalStateException("Optional is Empty"));
                questionStringBuilder = updateFetchQuestion(linkDto, questionResponseTemp);
            }
            if (commentResponseOptional.isPresent()) {
                CommentResponse commentResponseTemp =
                        commentResponseOptional.orElseThrow(() -> new IllegalStateException("Optional is Empty"));
                commentStringBuilder = updateFetchComment(linkDto, commentResponseTemp);
            }
            if (answersResponseOptional.isPresent()) {
                AnswersResponse answersResponseTemp =
                        answersResponseOptional.orElseThrow(() -> new IllegalStateException("Optional is Empty"));
                answerStringBuilder = updateFetchAnswers(linkDto, answersResponseTemp);
            }

            if (!answerStringBuilder.isEmpty() || !commentStringBuilder.isEmpty() || !questionStringBuilder.isEmpty()) {
                linkDto.lastUpdated(OffsetDateTime.now(ZoneId.systemDefault()));
                Link link = linkService
                        .findById(linkDto.id())
                        .orElseThrow(() -> new LinkNotFoundException("Ссылка с ID " + linkDto.id() + " не найдена"));
                link.updatedAt(OffsetDateTime.now(ZoneId.systemDefault()));
                linkService.update(link);

                StringBuilder temp = new StringBuilder();
                temp.append(CONST_SPACE)
                        .append(CONST_NEXT_LINE)
                        .append(CONST_SYMBOL)
                        .append(CONST_THEME_QUESTION);
                questionResponseOptional.ifPresent(questionResponse ->
                        temp.append(questionResponse.items().get(0).title()));
                temp.append(CONST_NEXT_LINE)
                        .append(answerStringBuilder)
                        .append(CONST_NEXT_LINE)
                        .append(commentStringBuilder)
                        .append(CONST_NEXT_LINE)
                        .append(questionStringBuilder)
                        .append(CONST_NEXT_LINE);

                linkDto.descriptionUpdate(temp.toString());
                updatedLinkList.add(linkDto);
            }
        });
    }

    public StringBuilder updateFetchQuestion(LinkDto linkDto, QuestionResponse questionResponse) {
        StringBuilder temp = new StringBuilder();

        if (!questionResponse.items().isEmpty()
                && linkDto.lastUpdated()
                        .isBefore(questionResponse.items().get(0).updatedAt())) {
            temp.append(CONST_SYMBOL).append(" Обновление: Просто изменен вопрос!\n");
        }

        return temp;
    }

    public StringBuilder updateFetchComment(LinkDto linkDto, CommentResponse commentResponse) {
        StringBuilder temp = new StringBuilder();
        for (CommentResponse.Comment item : commentResponse.items()) {
            if (linkDto.lastUpdated().isBefore(item.createdAt())) {
                temp.append(CONST_SYMBOL).append(" Обновление: Добавлен комментарий!\n");
                temp.append(CONST_SYMBOL)
                        .append(CONST_USER)
                        .append(item.owner().name())
                        .append(CONST_NEXT_LINE);
                temp.append(CONST_SYMBOL)
                        .append(CONST_CREATED_AT)
                        .append(item.createdAt())
                        .append(CONST_NEXT_LINE);
                temp.append(CONST_SYMBOL)
                        .append(CONST_COMMENT)
                        .append(item.text())
                        .append(CONST_NEXT_LINE);
            }
        }
        return temp;
    }

    public StringBuilder updateFetchAnswers(LinkDto linkDto, AnswersResponse answersResponse) {
        return answersResponse.items().stream()
                .filter(item -> linkDto.lastUpdated().isBefore(item.createdAt()))
                .collect(
                        StringBuilder::new,
                        (sb, item) -> sb.append(CONST_SYMBOL)
                                .append(" Обновление: Добавлен ответ!")
                                .append(CONST_NEXT_LINE)
                                .append(CONST_SYMBOL)
                                .append(CONST_USER)
                                .append(item.owner().name())
                                .append(CONST_NEXT_LINE)
                                .append(CONST_SYMBOL)
                                .append(CONST_CREATED_AT)
                                .append(item.createdAt())
                                .append(CONST_NEXT_LINE)
                                .append(CONST_SYMBOL)
                                .append(CONST_COMMENT)
                                .append(item.text())
                                .append(CONST_NEXT_LINE),
                        StringBuilder::append);
    }
}
