package backend.academy.scrapper.tracker.update;

import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.repository.ChatLinkRepository;
import backend.academy.scrapper.service.orm.OrmLinkService;
import backend.academy.scrapper.tracker.TelegramBotClient;
import backend.academy.scrapper.tracker.request.GitHubRequest;
import backend.academy.scrapper.tracker.request.StackOverFlowRequest;
import backend.academy.scrapper.tracker.response.github.IssueResponse;
import backend.academy.scrapper.tracker.response.github.PullRequestResponse;
import backend.academy.scrapper.tracker.response.stack.AnswersResponse;
import backend.academy.scrapper.tracker.response.stack.CommentResponse;
import backend.academy.scrapper.tracker.response.stack.QuestionResponse;
import backend.academy.scrapper.tracker.response.github.GitHubResponse;
import backend.academy.scrapper.tracker.GitHubClient;
import backend.academy.scrapper.tracker.StackOverFlowClient;
import backend.academy.scrapper.tracker.update.dto.LinkDto;
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
    private final OrmLinkService linkService;
    private final ChatLinkRepository chatLinkRepository;

    private List<LinkDto> updatedLinkList;

    private final static String CONST_GITHUB = "github";
    private final static String CONST_STACK_OVER_FLOW = "stackoverflow";


    public void updateLink(List<LinkDto> linkList) {
        System.err.println("Вход List<LinkDto> " + linkList);
        updatedLinkList = new ArrayList<>();
        for (LinkDto item : linkList) {
            String urlString = item.url().toString();

            if (urlString.contains(CONST_GITHUB)) {
                System.err.println("---------------Github");
                handlerUpdateGitHub(item);
            } else if (urlString.contains(CONST_STACK_OVER_FLOW)) {
                System.err.println("----------------StackOverFlow");
                handlerUpdateStackOverFlow(item);
            } else {
                throw new BadLinkRequestException(
                    "Ссылка не может быть обработана, " + "так как это не github и не stackoverflow");
            }
        }
        for (LinkDto item : updatedLinkList) {
            System.err.println("Отправка -------------------- Отправка");
            List<Long> chatIds = chatLinkRepository.findChatIdsByLinkId(item.id());
            telegramBotClient.addUpdate(new LinkUpdate(item.id(), item.url(), item.descriptionUpdate(), chatIds));
        }

    }



    private void handlerUpdateGitHub(LinkDto linkDto) {
        System.err.println("1 мы вошли");

        if (linkDto.lastUpdated() == null) {
            linkDto.lastUpdated(OffsetDateTime.now());
            Link link = linkService.findById(linkDto.id()).get();
            link.updatedAt(OffsetDateTime.now());
            linkService.update(link);
            System.err.println("1 Сменили время");

            return;
        }

        GitHubRequest gitHubRequest = parseUrl.parseUrlToGithubRequest(linkDto.url().toString());

        List<IssueResponse> issuesList = gitHubClient.fetchIssue(gitHubRequest, linkDto.lastUpdated());
        List<PullRequestResponse> pullRequestList = gitHubClient.fetchPullRequest(gitHubRequest, linkDto.lastUpdated());
        GitHubResponse gitHubResponse = gitHubClient.getFetchDate(gitHubRequest);

        StringBuilder issueStringBuilder = updateFetchIssue(linkDto, issuesList);
        StringBuilder pullRequestStringBuilder = updateFetchPullRequest(linkDto, pullRequestList);
        StringBuilder repositoryStringBuilder = updateFetchRepository(linkDto, gitHubResponse);

        if (!issueStringBuilder.isEmpty() || !pullRequestStringBuilder.isEmpty() || !repositoryStringBuilder.isEmpty()) {
            linkDto.lastUpdated(OffsetDateTime.now());

            Link link = linkService.findById(linkDto.id()).get();
            link.updatedAt(OffsetDateTime.now());
            linkService.update(link);

            StringBuilder temp = new StringBuilder();
            temp.append("----------------------").append("\n")
                .append("\uD83D\uDCE9").append(" Репозиторий: ").append(gitHubResponse.repositoryName()).append("\n")
                .append(pullRequestStringBuilder).append("\n")
                .append(issueStringBuilder).append("\n")
                .append(repositoryStringBuilder).append("\n");

            linkDto.descriptionUpdate(temp.toString());
            updatedLinkList.add(linkDto);
        }

    }


    private StringBuilder updateFetchRepository(LinkDto linkDto, GitHubResponse gitHubResponse) {
        StringBuilder temp = new StringBuilder();
        if (linkDto.lastUpdated().isBefore(gitHubResponse.updatedAt())) {
            temp.append("\uD83D\uDD39").append(" Обновление: Произошло изменения репозитория!\n");
        }
        return temp;
    }

    private StringBuilder updateFetchPullRequest(LinkDto linkDto, List<PullRequestResponse> pullRequestResponseList) {
        StringBuilder temp = new StringBuilder();
        for (PullRequestResponse item : pullRequestResponseList) {
            if (linkDto.lastUpdated().isBefore(item.updatedAt())) {
                temp.append("\uD83D\uDD39").append(" Обновление: Добавлен pullRequest!\n");
                temp.append("\uD83D\uDD39").append(" Название: ").append(item.title()).append("\n");
                temp.append("\uD83D\uDD39").append(" Пользователь: ").append(item.user().login()).append("\n");
                temp.append("\uD83D\uDD39").append(" Время создания: ").append(item.updatedAt()).append("\n");
                temp.append("\uD83D\uDD39").append(" Описание: ").append(item.text()).append("\n");
            }
        }
        return temp;
    }


    private StringBuilder updateFetchIssue(LinkDto linkDto, List<IssueResponse> issuesList) {
        StringBuilder temp = new StringBuilder();
        for (IssueResponse item : issuesList) {
            if (linkDto.lastUpdated().isBefore(item.updatedAt())) {
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

    private void handlerUpdateStackOverFlow(LinkDto linkDto) {

        if (linkDto.lastUpdated() == null) {
            linkDto.lastUpdated(OffsetDateTime.now());
            Link link = linkService.findById(linkDto.id()).get();
            link.updatedAt(OffsetDateTime.now());
            linkService.update(link);
            return;
        }

        StackOverFlowRequest stackOverFlowRequest = parseUrl.parseUrlToStackOverFlowRequest(linkDto.url().toString());

        QuestionResponse questionResponse = stackOverFlowClient.fetchQuestion(stackOverFlowRequest);
        CommentResponse commentResponse = stackOverFlowClient.fetchComment(stackOverFlowRequest);
        AnswersResponse answersResponse = stackOverFlowClient.fetchAnswer(stackOverFlowRequest);

        StringBuilder answerStringBuilder = updateFetchAnswers(linkDto, answersResponse);
        StringBuilder commentStringBuilder = updateFetchComment(linkDto, commentResponse);
        StringBuilder questionStringBuilder = updateFetchQuestion(linkDto, questionResponse);

        if (!answerStringBuilder.isEmpty() || !commentStringBuilder.isEmpty() || !questionStringBuilder.isEmpty()) {
            linkDto.lastUpdated(OffsetDateTime.now());
            Link link = linkService.findById(linkDto.id()).get();
            link.updatedAt(OffsetDateTime.now());
            linkService.update(link);


            StringBuilder temp = new StringBuilder();
            temp
                .append("----------------------").append("\n")
                .append("\uD83D\uDCE9").append("Темы вопроса: ").append(questionResponse.items().get(0).title()).append("\n")
                .append(answerStringBuilder).append("\n")
                .append(commentStringBuilder).append("\n")
                .append(questionStringBuilder).append("\n");


            linkDto.descriptionUpdate(temp.toString());
            updatedLinkList.add(linkDto);
        }
    }


    private StringBuilder updateFetchQuestion(LinkDto linkDto, QuestionResponse questionResponse) {
        StringBuilder temp = new StringBuilder();

        if (linkDto.lastUpdated().isBefore(questionResponse.items().get(0).updatedAt())) {
            temp.append("\uD83D\uDD39").append(" Обновление: Просто изменен вопрос!\n");
        }

        return temp;
    }

    private StringBuilder updateFetchComment(LinkDto linkDto, CommentResponse commentResponse) {
        StringBuilder temp = new StringBuilder();
        for (CommentResponse.Comment item : commentResponse.items()) {
            if (linkDto.lastUpdated().isBefore(item.createdAt())) {
                temp.append("\uD83D\uDD39").append(" Обновление: Добавлен комментарий!\n");
                temp.append("\uD83D\uDD39").append(" Пользователь: ").append(item.owner().name()).append("\n");
                temp.append("\uD83D\uDD39").append(" Время создания: ").append(item.createdAt()).append("\n");
                temp.append("\uD83D\uDD39").append(" Комментарий: ").append(item.text()).append("\n");
            }
        }
        return temp;
    }


    private StringBuilder updateFetchAnswers(LinkDto linkDto, AnswersResponse answersResponse) {
        return answersResponse.items().stream()
            .filter(item -> linkDto.lastUpdated().isBefore(item.createdAt()))
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
