package backend.academy.scrapper.tracker.client;


import backend.academy.scrapper.request.GitHubRequest;
import backend.academy.scrapper.response.GitHubResponse;
import backend.academy.scrapper.config.ScrapperConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import java.net.URI;
import java.util.regex.Pattern;

@Slf4j
public class GitHubClient {

    private final WebClient webClient;

    public GitHubClient(ScrapperConfig.GithubCredentials githubCredentials) {
        WebClient.Builder webClientBuilder = WebClient.builder()
            .baseUrl(githubCredentials.githubUrl());
        if (githubCredentials.githubToken() != null && !githubCredentials.githubToken().trim().isEmpty()) {
            webClientBuilder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubCredentials.githubToken());
        }
        this.webClient = webClientBuilder.build();
    }

    public GitHubResponse getFetchDate(GitHubRequest gitHubRequest) {
        log.info("GitHubClient getFetchDate {}", gitHubRequest);
        return webClient
            .get().uri(uriBuilder -> uriBuilder
                .path("/{userName}/{repositoryName}")
                .build(gitHubRequest.userName(), gitHubRequest.repositoryName()))
            .retrieve()
            .bodyToMono(GitHubResponse.class)
            .block();
    }
}
