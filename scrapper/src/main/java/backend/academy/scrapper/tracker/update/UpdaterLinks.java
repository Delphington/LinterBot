package backend.academy.scrapper.tracker.update;

import backend.academy.scrapper.api.service.LinkService;
import backend.academy.scrapper.request.GitHubRequest;
import backend.academy.scrapper.request.StackOverFlowRequest;
import backend.academy.scrapper.tracker.client.github.IssueResponse;
import backend.academy.scrapper.tracker.client.github.PullRequestResponse;
import backend.academy.scrapper.tracker.client.stack.AnswersResponse;
import backend.academy.scrapper.tracker.client.stack.CommentResponse;
import backend.academy.scrapper.tracker.client.stack.QuestionResponse;
import backend.academy.scrapper.tracker.client.github.GitHubResponse;
import backend.academy.scrapper.tracker.client.github.GitHubClient;
import backend.academy.scrapper.tracker.client.stack.StackOverFlowClient;
import backend.academy.scrapper.tracker.client.TelegramBotClient;
import backend.academy.scrapper.tracker.update.dto.Link;
import backend.academy.scrapper.tracker.update.exception.BadLinkRequestException;
import backend.academy.scrapper.tracker.update.model.LinkUpdate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UpdaterLinks {
    private final TelegramBotClient telegramBotClient;

    private final ParseUrl parseUrl;

    private final GitHubClient gitHubClient;
    private final StackOverFlowClient stackOverFlowClient;
    private final LinkService linkService;

    private List<Link> updatedLinkList;

    public void updateLink(List<Link> linkList) {
        updatedLinkList = new ArrayList<>();
        for (Link link : linkList) {
            if (link.url().toString().contains("github")) {
                handlerUpdateGitHub(link);
            } else if (link.url().toString().contains("stackoverflow")) {
                handlerUpdateStackOverFlow(link);
            } else {
                throw new BadLinkRequestException(
                    "Ссылка не может быть обработана, " + "так как это не github и не stackoverflow");
            }
        }

        for (Link item : updatedLinkList) {
            List<Long> chatIds = linkService.findIdChatsByUrlId(item.id());
            telegramBotClient.addUpdate(new LinkUpdate(item.id(), item.url(), item.descriptionUpdate(), chatIds));
        }

    }


    private void handlerUpdateGitHub(Link link) {
        if (link.lastUpdatedTime() == null) {
            link.lastUpdatedTime(OffsetDateTime.now());
            return;
        }

        GitHubRequest gitHubRequest = parseUrl.parseUrlToGithubRequest(link.url().toString());

        List<IssueResponse> issuesList = gitHubClient.fetchIssue(gitHubRequest, link.lastUpdatedTime());
        List<PullRequestResponse> pullRequestList = gitHubClient.fetchPullRequest(gitHubRequest, link.lastUpdatedTime());
        GitHubResponse gitHubResponse = gitHubClient.getFetchDate(gitHubRequest);

        StringBuilder issueStringBuilder = updateFetchIssue(link, issuesList);
        StringBuilder pullRequestStringBuilder = updateFetchPullRequest(link, pullRequestList);
        StringBuilder repositoryStringBuilder = updateFetchRepository(link, gitHubResponse);

        if (!issueStringBuilder.isEmpty() || !pullRequestStringBuilder.isEmpty() || !repositoryStringBuilder.isEmpty()) {
            link.lastUpdatedTime(OffsetDateTime.now());

            StringBuilder temp = new StringBuilder();
            temp.append("----------------------").append("\n")
                .append("\uD83D\uDCE9").append(" Репозиторий: ").append(gitHubResponse.repositoryName()).append("\n")
                .append(pullRequestStringBuilder).append("\n")
                .append(issueStringBuilder).append("\n")
                .append(repositoryStringBuilder).append("\n");

            link.descriptionUpdate(temp.toString());
            updatedLinkList.add(link);
        }

    }


    private StringBuilder updateFetchRepository(Link link, GitHubResponse gitHubResponse) {
        StringBuilder temp = new StringBuilder();
        if (link.lastUpdatedTime().isBefore(gitHubResponse.updatedAt())) {
            temp.append("\uD83D\uDD39").append(" Обновление: Произошло изменения репозитория!\n");
        }
        return temp;
    }

    private StringBuilder updateFetchPullRequest(Link link, List<PullRequestResponse> pullRequestResponseList) {
        StringBuilder temp = new StringBuilder();
        for (PullRequestResponse item : pullRequestResponseList) {
            if (link.lastUpdatedTime().isBefore(item.updatedAt())) {
                temp.append("\uD83D\uDD39").append(" Обновление: Добавлен pullRequest!\n");
                temp.append("\uD83D\uDD39").append(" Название: ").append(item.title()).append("\n");
                temp.append("\uD83D\uDD39").append(" Пользователь: ").append(item.user().login()).append("\n");
                temp.append("\uD83D\uDD39").append(" Время создания: ").append(item.updatedAt()).append("\n");
                temp.append("\uD83D\uDD39").append(" Описание: ").append(item.text()).append("\n");
            }
        }
        return temp;
    }


    private StringBuilder updateFetchIssue(Link link, List<IssueResponse> issuesList) {
        StringBuilder temp = new StringBuilder();
        for (IssueResponse item : issuesList) {
            if (link.lastUpdatedTime().isBefore(item.updatedAt())) {
                temp.append("\uD83D\uDD39").append(" Обновление: Добавлен issue!\n");
                temp.append("\uD83D\uDD39").append(" Название: ").append(item.title()).append("\n");
                temp.append("\uD83D\uDD39").append(" Пользователь: ").append(item.user().login()).append("\n");
                temp.append("\uD83D\uDD39").append(" Время создания: ").append(item.updatedAt()).append("\n");
                temp.append("\uD83D\uDD39").append(" Описание: ").append(item.text()).append("\n");
            }
        }
        return temp;
    }


    //Вопрос: https://api.stackexchange.com/2.3/questions/79486408?order=desc&sort=activity&site=stackoverflow
    //Коммент https://api.stackexchange.com/2.3/questions/79486408/comments?site=stackoverflow&filter=withbody

    private void handlerUpdateStackOverFlow(Link link) {

        if (link.lastUpdatedTime() == null) {
            link.lastUpdatedTime(OffsetDateTime.now());
            return;
        }

        StackOverFlowRequest stackOverFlowRequest = parseUrl.parseUrlToStackOverFlowRequest(link.url().toString());

        QuestionResponse questionResponse = stackOverFlowClient.fetchQuestion(stackOverFlowRequest);
        CommentResponse commentResponse = stackOverFlowClient.fetchComment(stackOverFlowRequest);
        AnswersResponse answersResponse = stackOverFlowClient.fetchAnswer(stackOverFlowRequest);

        StringBuilder answerStringBuilder = updateFetchAnswers(link, answersResponse);
        StringBuilder commentStringBuilder = updateFetchComment(link, commentResponse);
        StringBuilder questionStringBuilder = updateFetchQuestion(link, questionResponse);

        if (!answerStringBuilder.isEmpty() || !commentStringBuilder.isEmpty() || !questionStringBuilder.isEmpty()) {
            link.lastUpdatedTime(OffsetDateTime.now());

            StringBuilder temp = new StringBuilder();
            temp
                .append("----------------------").append("\n")
                .append("\uD83D\uDCE9").append("Темы вопроса: ").append(questionResponse.items().get(0).title()).append("\n")
                .append(answerStringBuilder).append("\n")
                .append(commentStringBuilder).append("\n")
                .append(questionStringBuilder).append("\n");

            link.descriptionUpdate(temp.toString());
            updatedLinkList.add(link);
        }
    }


    private StringBuilder updateFetchQuestion(Link link, QuestionResponse questionResponse) {
        StringBuilder temp = new StringBuilder();

        if (link.lastUpdatedTime().isBefore(questionResponse.items().get(0).updatedAt())) {
            temp.append("\uD83D\uDD39").append(" Обновление: Просто изменен вопрос!\n");
        }

        return temp;
    }

    private StringBuilder updateFetchComment(Link link, CommentResponse commentResponse) {
        StringBuilder temp = new StringBuilder();
        for (CommentResponse.Comment item : commentResponse.items()) {
            if (link.lastUpdatedTime().isBefore(item.createdAt())) {
                temp.append("\uD83D\uDD39").append(" Обновление: Добавлен комментарий!\n");
                temp.append("\uD83D\uDD39").append(" Пользователь: ").append(item.owner().name()).append("\n");
                temp.append("\uD83D\uDD39").append(" Время создания: ").append(item.createdAt()).append("\n");
                temp.append("\uD83D\uDD39").append(" Комментарий: ").append(item.text()).append("\n");
            }
        }
        return temp;
    }


    private StringBuilder updateFetchAnswers(Link link, AnswersResponse answersResponse) {
        return answersResponse.items().stream()
            .filter(item -> link.lastUpdatedTime().isBefore(item.createdAt()))
            .collect(
                StringBuilder::new,
                (sb, item) ->
                    sb.append("\uD83D\uDD39").append(" Обновление: Добавлен ответ!").append("\n")
                        .append("\uD83D\uDD39").append(" Пользователь: ").append(item.owner().name()).append("\n")
                        .append("\uD83D\uDD39").append(" Время создания: ").append(item.createdAt()).append("\n")
                        .append("\uD83D\uDD39").append(" Комментарий: ").append(item.text()).append("\n"),
                StringBuilder::append
            );
    }
}
