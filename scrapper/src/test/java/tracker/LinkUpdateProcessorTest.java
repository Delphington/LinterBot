package tracker;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import backend.academy.scrapper.client.TgBotClient;
import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.repository.TgChatLinkRepository;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.tracker.client.GitHubClient;
import backend.academy.scrapper.tracker.client.StackOverFlowClient;
import backend.academy.scrapper.tracker.response.github.GitHubResponse;
import backend.academy.scrapper.tracker.response.github.IssueResponse;
import backend.academy.scrapper.tracker.response.github.PullRequestResponse;
import backend.academy.scrapper.tracker.response.stack.AnswersResponse;
import backend.academy.scrapper.tracker.response.stack.CommentResponse;
import backend.academy.scrapper.tracker.response.stack.QuestionResponse;
import backend.academy.scrapper.tracker.update.LinkUpdateProcessor;
import backend.academy.scrapper.tracker.update.dto.LinkDto;
import backend.academy.scrapper.tracker.update.exception.BadLinkRequestException;
import backend.academy.scrapper.tracker.update.model.LinkUpdate;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LinkUpdateProcessorTest {

    @Mock
    private TgBotClient tgBotClient;

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private StackOverFlowClient stackOverFlowClient;

    @Mock
    private LinkService linkService;

    @Mock
    private TgChatLinkRepository tgChatLinkRepository;

    @InjectMocks
    private LinkUpdateProcessor linkUpdateProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateLink_GitHub() {
        LinkDto linkDto = new LinkDto();
        linkDto.id(1L);
        linkDto.url(URI.create("https://github.com/user/repo"));
        linkDto.lastUpdated(OffsetDateTime.now());

        when(linkService.findById(anyLong())).thenReturn(Optional.of(new Link()));
        when(gitHubClient.fetchIssue(any(), any())).thenReturn(Collections.emptyList());
        when(gitHubClient.fetchPullRequest(any(), any())).thenReturn(Collections.emptyList());
        when(gitHubClient.getFetchDate(any())).thenReturn(new GitHubResponse("repo", OffsetDateTime.now()));

        linkUpdateProcessor.updateLink(List.of(linkDto));

        verify(tgBotClient, times(1)).addUpdate(any(LinkUpdate.class));
    }

    @Test
    void testUpdateLink_StackOverFlow() {
        LinkDto linkDto = new LinkDto();
        linkDto.id(1L);
        linkDto.url(URI.create("https://stackoverflow.com/questions/12345"));
        linkDto.lastUpdated(OffsetDateTime.now());

        when(linkService.findById(anyLong())).thenReturn(Optional.of(new Link()));
        when(stackOverFlowClient.fetchQuestion(any())).thenReturn(new QuestionResponse(Collections.emptyList()));
        when(stackOverFlowClient.fetchComment(any())).thenReturn(new CommentResponse(Collections.emptyList()));
        when(stackOverFlowClient.fetchAnswer(any())).thenReturn(new AnswersResponse(Collections.emptyList()));

        linkUpdateProcessor.updateLink(List.of(linkDto));
        when(stackOverFlowClient.fetchQuestion(any()))
                .thenReturn(
                        new QuestionResponse(List.of(new QuestionResponse.QuestionItem(OffsetDateTime.now(), null))));
        //   verify(telegramBotClient, times(1)).addUpdate(any(LinkUpdate.class));
    }

    @Test
    void testUpdateLink_InvalidLink() {
        LinkDto linkDto = new LinkDto();
        linkDto.id(1L);
        linkDto.url(URI.create("https://invalid.com"));
        linkDto.lastUpdated(OffsetDateTime.now());

        assertThrows(BadLinkRequestException.class, () -> linkUpdateProcessor.updateLink(List.of(linkDto)));
    }

    @Test
    void testHandlerUpdateGitHub_WithUpdates() {
        LinkDto linkDto = new LinkDto();
        linkDto.id(1L);
        linkDto.url(URI.create("https://github.com/user/repo"));
        linkDto.lastUpdated(OffsetDateTime.now().minusDays(1));

        // Мокирование linkService.findById
        when(linkService.findById(anyLong())).thenReturn(Optional.of(new Link()));

        // Мокирование GitHub клиента
        IssueResponse issueResponse =
                new IssueResponse("Issue Title", new IssueResponse.User("user"), OffsetDateTime.now(), "Issue body");
        PullRequestResponse pullRequestResponse = new PullRequestResponse(
                "PR Title", new PullRequestResponse.User("user"), OffsetDateTime.now(), "PR body");
        GitHubResponse gitHubResponse = new GitHubResponse("repo", OffsetDateTime.now());

        when(gitHubClient.fetchIssue(any(), any())).thenReturn(List.of(issueResponse));
        when(gitHubClient.fetchPullRequest(any(), any())).thenReturn(List.of(pullRequestResponse));
        when(gitHubClient.getFetchDate(any())).thenReturn(gitHubResponse);

        linkUpdateProcessor.handlerUpdateGitHub(linkDto);

        // Проверяем, что список обновлений не пуст
        assertFalse(linkUpdateProcessor.updatedLinkList().isEmpty());
    }

    @Test
    void testHandlerUpdateStackOverFlow_NoUpdates() {
        LinkDto linkDto = new LinkDto();
        linkDto.id(1L);
        linkDto.url(URI.create("https://stackoverflow.com/questions/12345"));
        linkDto.lastUpdated(OffsetDateTime.now());

        when(stackOverFlowClient.fetchQuestion(any())).thenReturn(new QuestionResponse(Collections.emptyList()));
        when(stackOverFlowClient.fetchComment(any())).thenReturn(new CommentResponse(Collections.emptyList()));
        when(stackOverFlowClient.fetchAnswer(any())).thenReturn(new AnswersResponse(Collections.emptyList()));

        linkUpdateProcessor.handlerUpdateStackOverFlow(linkDto);

        assertTrue(linkUpdateProcessor.updatedLinkList().isEmpty());
    }

    @Test
    void testUpdateFetchRepository() {
        LinkDto linkDto = new LinkDto();
        linkDto.id(1L);
        linkDto.url(URI.create("https://github.com/user/repo"));
        linkDto.lastUpdated(OffsetDateTime.now().minusDays(1));

        GitHubResponse gitHubResponse = new GitHubResponse("repo", OffsetDateTime.now());

        StringBuilder result = linkUpdateProcessor.updateFetchRepository(linkDto, gitHubResponse);

        assertFalse(result.isEmpty());
    }

    @Test
    void testUpdateFetchPullRequest() {
        LinkDto linkDto = new LinkDto();
        linkDto.id(1L);
        linkDto.url(URI.create("https://github.com/user/repo"));
        linkDto.lastUpdated(OffsetDateTime.now().minusDays(1));

        PullRequestResponse pullRequestResponse = new PullRequestResponse(
                "PR Title", new PullRequestResponse.User("user"), OffsetDateTime.now(), "PR body");

        StringBuilder result = linkUpdateProcessor.updateFetchPullRequest(linkDto, List.of(pullRequestResponse));

        assertFalse(result.isEmpty());
    }

    @Test
    void testUpdateFetchIssue() {
        LinkDto linkDto = new LinkDto();
        linkDto.id(1L);
        linkDto.url(URI.create("https://github.com/user/repo"));
        linkDto.lastUpdated(OffsetDateTime.now().minusDays(1));

        IssueResponse issueResponse =
                new IssueResponse("Issue Title", new IssueResponse.User("user"), OffsetDateTime.now(), "Issue body");

        StringBuilder result = linkUpdateProcessor.updateFetchIssue(linkDto, List.of(issueResponse));

        assertFalse(result.isEmpty());
    }

    @Test
    void testUpdateFetchQuestion() {
        LinkDto linkDto = new LinkDto();
        linkDto.id(1L);
        linkDto.url(URI.create("https://stackoverflow.com/questions/12345"));
        linkDto.lastUpdated(OffsetDateTime.now().minusDays(1));

        QuestionResponse.QuestionItem questionItem =
                new QuestionResponse.QuestionItem(OffsetDateTime.now(), "Question Title");
        QuestionResponse questionResponse = new QuestionResponse(List.of(questionItem));

        StringBuilder result = linkUpdateProcessor.updateFetchQuestion(linkDto, questionResponse);

        assertFalse(result.isEmpty());
    }

    @Test
    void testUpdateFetchComment() {
        LinkDto linkDto = new LinkDto();
        linkDto.id(1L);
        linkDto.url(URI.create("https://stackoverflow.com/questions/12345"));
        linkDto.lastUpdated(OffsetDateTime.now().minusDays(1));

        CommentResponse.Comment comment =
                new CommentResponse.Comment(new CommentResponse.Owner("user"), OffsetDateTime.now(), "Comment body");
        CommentResponse commentResponse = new CommentResponse(List.of(comment));

        StringBuilder result = linkUpdateProcessor.updateFetchComment(linkDto, commentResponse);

        assertFalse(result.isEmpty());
    }

    @Test
    void testUpdateFetchAnswers_NoUpdates() {
        LinkDto linkDto = new LinkDto();
        linkDto.id(1L);
        linkDto.url(URI.create("https://stackoverflow.com/questions/12345"));
        linkDto.lastUpdated(OffsetDateTime.now());

        // Создаем пустой ответ
        AnswersResponse answersResponse = new AnswersResponse(Collections.emptyList());

        // Выполняем метод
        StringBuilder result = linkUpdateProcessor.updateFetchAnswers(linkDto, answersResponse);

        // Проверяем, что результат пустой
        assertTrue(result.isEmpty());
    }
}
