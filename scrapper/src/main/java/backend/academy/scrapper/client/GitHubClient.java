package backend.academy.scrapper.client;


import backend.academy.scrapper.request.GitHubRequest;
import backend.academy.scrapper.response.GitHubResponse;
import backend.academy.scrapper.config.ScrapperConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;


public class GitHubClient {

    private final ScrapperConfig.GithubCredentials githubCredentials;
    private WebClient webClient;

    public GitHubClient(ScrapperConfig.GithubCredentials githubCredentials) {
        this.githubCredentials = githubCredentials;
//
//        if(githubCredentials.githubToken()!= null && githubCredentials.githubToken().length()>3){
//            this.webClient = WebClient.builder()
//                .baseUrl(githubCredentials.githubUrl())
//                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubCredentials.githubToken())
//                .build();
//        }else{
            this.webClient = WebClient.builder().baseUrl(githubCredentials.githubUrl()).build();
       // }
    }

    public GitHubResponse getFetchDate(GitHubRequest gitHubRequest) {
        var client = webClient
            .get().uri(uriBuilder -> uriBuilder
                .path("/{userName}/{repositoryName}")
                .build(gitHubRequest.userName(), gitHubRequest.repositoryName()))
            .retrieve()
            .bodyToMono(GitHubResponse.class)
            .block();

        return client;
    }
}
