package tracker;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.scrapper.client.UpdateSender;
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
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LinkUpdateProcessorTest {

    @Mock
    private UpdateSender tgBotClient;

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
    void testUpdateLink_InvalidLink() {
        LinkDto linkDto = new LinkDto();
        linkDto.id(1L);
        linkDto.url(URI.create("https://invalid.com"));
        linkDto.lastUpdated(OffsetDateTime.now());

        assertThrows(BadLinkRequestException.class, () -> linkUpdateProcessor.updateLink(List.of(linkDto)));
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
