// package tracker.github;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;
//
// import backend.academy.scrapper.config.ScrapperConfig;
// import backend.academy.scrapper.request.GitHubRequest;
// import backend.academy.scrapper.tracker.github.GitHubResponse;
// import backend.academy.scrapper.tracker.client.github.GitHubClient;
// import java.time.OffsetDateTime;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.web.reactive.function.client.WebClient;
// import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
// import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
// import reactor.core.publisher.Mono;
//
// public class GitHubClientTest {
//
//    @Test
//    @DisplayName("Получение данных о репозитории: успешный ответ")
//    public void getFetchDate_ShouldReturnCorrectInformation() {
//        // Создаем мок WebClient
//        WebClient webClient = mock(WebClient.class);
//        RequestHeadersUriSpec requestHeadersUriSpec = mock(RequestHeadersUriSpec.class);
//        ResponseSpec responseSpec = mock(ResponseSpec.class);
//
//        // Настраиваем мок
//        when(webClient.get()).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.uri(any(String.class), any(String.class), any(String.class)))
//                .thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(GitHubResponse.class))
//                .thenReturn(Mono.just(new GitHubResponse(
//                        123L,
//                        "Delphington/linktracker",
//                        "Delphington",
//                        OffsetDateTime.now(),
//                        OffsetDateTime.now(),
//                        OffsetDateTime.now(),
//                        "Java",
//                        new GitHubResponse.Owner("Delphington", 456L, "https://example.com/example"))));
//
//        // Создаем клиент с моком WebClient
//        ScrapperConfig.GithubCredentials credentials =
//                new ScrapperConfig.GithubCredentials("https://api.github.com", "test-token");
//        GitHubClient client = new GitHubClient(credentials) {
//            @Override
//            public GitHubResponse getFetchDate(GitHubRequest gitHubRequest) {
//                return webClient
//                        .get()
//                        .uri(
//                                "/repos/{userName}/{repositoryName}",
//                                gitHubRequest.userName(),
//                                gitHubRequest.repositoryName())
//                        .retrieve()
//                        .bodyToMono(GitHubResponse.class)
//                        .block();
//            }
//        };
//
//        // Выполняем запрос
//        GitHubRequest request = new GitHubRequest("Delphington", "linktracker");
//        GitHubResponse response = client.getFetchDate(request);
//
//        // Проверяем результат
//        assertNotNull(response);
//        assertEquals(123L, response.repositoryId());
//        assertEquals("Delphington/linktracker", response.fullName());
//        assertEquals("Delphington", response.description());
//        assertEquals("Java", response.language());
//        assertEquals("Delphington", response.owner().login());
//
//        // Проверяем, что методы мока были вызваны
//        verify(webClient).get();
//        verify(requestHeadersUriSpec).uri("/repos/{userName}/{repositoryName}", "Delphington", "linktracker");
//        verify(requestHeadersUriSpec).retrieve();
//        verify(responseSpec).bodyToMono(GitHubResponse.class);
//    }
//
//    @Test
//    @DisplayName("Возвращает null в случае не найденного запроса")
//    public void getFetchDate_ShouldReturnNullWhenRepositoryNotFound() {
//        // Создаем мок WebClient
//        WebClient webClient = mock(WebClient.class);
//        RequestHeadersUriSpec requestHeadersUriSpec = mock(RequestHeadersUriSpec.class);
//        ResponseSpec responseSpec = mock(ResponseSpec.class);
//
//        when(webClient.get()).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.uri(any(String.class), any(String.class), any(String.class)))
//                .thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(GitHubResponse.class)).thenReturn(Mono.empty());
//
//        ScrapperConfig.GithubCredentials credentials =
//                new ScrapperConfig.GithubCredentials("https://api.github.com", "test-token");
//        GitHubClient client = new GitHubClient(credentials) {
//            @Override
//            public GitHubResponse getFetchDate(GitHubRequest gitHubRequest) {
//                return webClient
//                        .get()
//                        .uri(
//                                "/repos/{userName}/{repositoryName}",
//                                gitHubRequest.userName(),
//                                gitHubRequest.repositoryName())
//                        .retrieve()
//                        .bodyToMono(GitHubResponse.class)
//                        .block();
//            }
//        };
//
//        GitHubRequest request = new GitHubRequest("jij", "hih");
//        GitHubResponse response = client.getFetchDate(request);
//
//        // Проверяем результат
//        assertNull(response);
//
//        verify(webClient).get();
//        verify(requestHeadersUriSpec).uri("/repos/{userName}/{repositoryName}", "jij", "hih");
//        verify(requestHeadersUriSpec).retrieve();
//        verify(responseSpec).bodyToMono(GitHubResponse.class);
//    }
// }
