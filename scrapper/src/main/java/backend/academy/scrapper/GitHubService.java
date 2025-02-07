package backend.academy.scrapper;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
@Setter
@Component
public class GitHubService {
    private WebClient webClient;

    public GitHubResponse getFetchDate() {
        webClient = WebClient.builder().baseUrl("https://api.github.com/repos/").build();
        String userName = "foblako";
        String rep = "EndlessSurvivors";

        var client = webClient
            .get().uri("foblako/EndlessSurvivors", userName, rep)
            .retrieve()
            .bodyToMono(GitHubResponse.class)
            .block();

        return client;

    }
}

//
//    public GitHubService(String gitHubToken) {
//        this.gitHubToken = githubToken;
/// /        this.webClient = WebClient.builder()
/// /            .baseUrl("https://api.github.com")
/// /            .defaultHeader("Authorization", "Bearer " + githubToken)  //Добавляем токен в заголовок
/// /            .build();
//    }
