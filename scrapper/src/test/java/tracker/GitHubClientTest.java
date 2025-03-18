package tracker;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import backend.academy.scrapper.configuration.ScrapperConfig;
import backend.academy.scrapper.tracker.client.GitHubClient;
import backend.academy.scrapper.tracker.request.GitHubRequest;
import backend.academy.scrapper.tracker.response.github.IssueResponse;
import backend.academy.scrapper.tracker.response.github.PullRequestResponse;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class GitHubClientTest {

    WebClient webClient = mock(WebClient.class);
    WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
    WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    private GitHubClient gitHubClient;

    @BeforeEach
    void setUp() throws Exception {
        webClient = mock(WebClient.class);
        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        // Настраиваем моки
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // Создаем клиент
        ScrapperConfig.GithubCredentials credentials = new ScrapperConfig.GithubCredentials(
                "https://api.github.com", // Базовый URL
                "test-token");
        gitHubClient = new GitHubClient(credentials);

        Field webClientField = GitHubClient.class.getSuperclass().getDeclaredField("webClient");
        webClientField.setAccessible(true);
        webClientField.set(gitHubClient, webClient);
    }

    @Test
    @DisplayName("fetchPullRequest: возвращает пустой список, если since = null")
    void fetchPullRequest_ShouldReturnEmptyList_WhenSinceIsNull() {
        // Вызов метода с since = null
        GitHubRequest request = new GitHubRequest("user", "repo");
        List<PullRequestResponse> result = gitHubClient.fetchPullRequest(request, null);

        // Проверки
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Проверка, что WebClient не вызывался
        verify(webClient, never()).get();
    }

    @Test
    @DisplayName("fetchIssue: возвращает пустой список, если since = null")
    void fetchIssue_ShouldReturnEmptyList_WhenSinceIsNull() {
        GitHubRequest request = new GitHubRequest("user", "repo");
        List<IssueResponse> result = gitHubClient.fetchIssue(request, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(webClient, never()).get();
    }
}
