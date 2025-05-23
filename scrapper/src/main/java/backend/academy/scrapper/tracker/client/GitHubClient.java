package backend.academy.scrapper.tracker.client;

import backend.academy.scrapper.configuration.ScrapperConfig;
import backend.academy.scrapper.configuration.api.WebClientProperties;
import backend.academy.scrapper.tracker.request.GitHubRequest;
import backend.academy.scrapper.tracker.response.github.GitHubResponse;
import backend.academy.scrapper.tracker.response.github.IssueResponse;
import backend.academy.scrapper.tracker.response.github.PullRequestResponse;
import io.github.resilience4j.retry.annotation.Retry;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

/**
 * было https://github.com/Delphington/TestApiGitHubs/pull/1 стало
 * https://api.github.com/repos/Delphington/TestApiGitHubs/pulls/1
 *
 * <p>было https://github.com/Delphington/TestApiGitHubs стало https://api.github.com/repos/Delphington/TestApiGitHubs
 *
 * <p>было https://github.com/Delphington/TestApiGitHubs/issues/2 стало
 * https://api.github.com/repos/Delphington/TestApiGitHubs/issues/2 https://api.github.com/repos/Delphington/Delphington
 */

/// **

@Slf4j
public class GitHubClient extends BaseWebClient {

    public GitHubClient(ScrapperConfig.GithubCredentials githubCredentials, WebClientProperties webClientProperties) {
        super(githubCredentials.githubUrl(), webClientProperties);
        if (githubCredentials.githubToken() != null
                && !githubCredentials.githubToken().trim().isEmpty()) {
            webClient.mutate().defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubCredentials.githubToken());
        }
    }

    @Retry(name = "getFetchDateGitHub", fallbackMethod = "getFetchDateFallback")
    public Optional<GitHubResponse> getFetchDate(GitHubRequest gitHubRequest) {
        log.info("GitHubClient getFetchDate {}", gitHubRequest);
        return Optional.ofNullable(webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{userName}/{repositoryName}")
                        .build(gitHubRequest.userName(), gitHubRequest.repositoryName()))
                .retrieve()
                .bodyToMono(GitHubResponse.class)
                .timeout(webClientProperties.globalTimeout())
                .block());
    }

    @Retry(name = "fetchPullRequestGitHub", fallbackMethod = "fetchPullRequestFallback")
    public Optional<List<PullRequestResponse>> fetchPullRequest(GitHubRequest gitHubRequest, OffsetDateTime since) {
        if (since == null) {
            return Optional.of(Collections.emptyList());
        }

        List<PullRequestResponse> list = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{userName}/{repositoryName}/pulls")
                        .build(gitHubRequest.userName(), gitHubRequest.repositoryName()))
                .retrieve()
                .bodyToFlux(PullRequestResponse.class)
                .collectList()
                .timeout(webClientProperties.globalTimeout())
                .blockOptional()
                .orElse(Collections.emptyList());

        return Optional.of(
                list.stream().filter(i -> i.updatedAt().isAfter(since)).collect(Collectors.toList()));
    }

    @Retry(name = "fetchIssueGitHub", fallbackMethod = "fetchIssueFallback")
    public Optional<List<IssueResponse>> fetchIssue(GitHubRequest gitHubRequest, OffsetDateTime since) {
        if (since == null) {
            return Optional.of(Collections.emptyList());
        }

        List<IssueResponse> list = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{userName}/{repositoryName}/issues")
                        .build(gitHubRequest.userName(), gitHubRequest.repositoryName()))
                .retrieve()
                .bodyToFlux(IssueResponse.class)
                .collectList()
                .timeout(webClientProperties.globalTimeout())
                .blockOptional()
                .orElse(Collections.emptyList());

        log.debug("GitHubClient Issue {}", gitHubRequest);

        return Optional.of(
                list.stream().filter(i -> i.updatedAt().isAfter(since)).collect(Collectors.toList()));
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private Optional<List<PullRequestResponse>> fetchPullRequestFallback(
            GitHubRequest request, OffsetDateTime since, Exception ex) {
        log.error(
                "Ошибка при получении PullRequest для репозитория {}, request = {}, since = {}",
                ex.getMessage(),
                request,
                since);
        return Optional.empty();
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private Optional<List<IssueResponse>> fetchIssueFallback(
            GitHubRequest request, OffsetDateTime since, Exception ex) {
        log.error(
                "Ошибка при получении Issues для репозитория {}, request = {}, since = {}",
                ex.getMessage(),
                request,
                since);
        return Optional.empty();
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private Optional<GitHubResponse> getFetchDateFallback(GitHubRequest request, Exception ex) {
        log.error("Ошибка при получении даты для репозитория, request = {}, ex = {}", request, ex.getMessage());
        return Optional.empty();
    }
}
