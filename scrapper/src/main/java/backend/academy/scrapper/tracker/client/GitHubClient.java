package backend.academy.scrapper.tracker.client;

import backend.academy.scrapper.configuration.ScrapperConfig;
import backend.academy.scrapper.tracker.request.GitHubRequest;
import backend.academy.scrapper.tracker.response.github.GitHubResponse;
import backend.academy.scrapper.tracker.response.github.IssueResponse;
import backend.academy.scrapper.tracker.response.github.PullRequestResponse;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

/**
 было     https://github.com/Delphington/TestApiGitHubs/pull/1
 стало    https://api.github.com/repos/Delphington/TestApiGitHubs/pulls/1

 было    https://github.com/Delphington/TestApiGitHubs
 стало   https://api.github.com/repos/Delphington/TestApiGitHubs

 было   https://github.com/Delphington/TestApiGitHubs/issues/2
 стало  https://api.github.com/repos/Delphington/TestApiGitHubs/issues/2
 https://api.github.com/repos/Delphington/Delphington
 * */
///**

@Slf4j
public class GitHubClient extends BaseWebClient {

    public GitHubClient(ScrapperConfig.GithubCredentials githubCredentials) {
        super(WebClient.builder(), githubCredentials.githubUrl());
        if (githubCredentials.githubToken() != null && !githubCredentials.githubToken().trim().isEmpty()) {
            webClient.mutate().defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubCredentials.githubToken());
        }
    }

    public GitHubResponse getFetchDate(GitHubRequest gitHubRequest) {
        log.info("GitHubClient getFetchDate {}", gitHubRequest);
        return webClient
            .get()
            .uri(uriBuilder -> uriBuilder
                .path("/{userName}/{repositoryName}")
                .build(gitHubRequest.userName(), gitHubRequest.repositoryName()))
            .retrieve()
            .bodyToMono(GitHubResponse.class)
            .block();
    }


    public List<PullRequestResponse> fetchPullRequest(GitHubRequest gitHubRequest, OffsetDateTime since) {
        if (since == null) {
            return Collections.emptyList();
        }

        List<PullRequestResponse> list = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/{userName}/{repositoryName}/pulls")
                .build(gitHubRequest.userName(), gitHubRequest.repositoryName())
            )
            .retrieve()
            .bodyToFlux(PullRequestResponse.class)
            .collectList()
            .blockOptional()
            .orElse(Collections.emptyList());

        return list.stream()
            .filter(i -> i.updatedAt().isAfter(since))
            .collect(Collectors.toList());
    }

    public List<IssueResponse> fetchIssue(GitHubRequest gitHubRequest, OffsetDateTime since) {
        if (since == null) {
            return Collections.emptyList();
        }

        List<IssueResponse> list = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/{userName}/{repositoryName}/issues")
                .build(gitHubRequest.userName(), gitHubRequest.repositoryName())
            )
            .retrieve()
            .bodyToFlux(IssueResponse.class)
            .collectList()
            .blockOptional()
            .orElse(Collections.emptyList());

        log.debug("GitHubClient Issue {}", gitHubRequest);

        return list.stream()
            .filter(i -> i.updatedAt().isAfter(since))
            .collect(Collectors.toList());
    }
}
