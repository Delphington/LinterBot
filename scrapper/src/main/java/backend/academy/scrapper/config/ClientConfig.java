package backend.academy.scrapper.config;

import backend.academy.scrapper.client.GitHubClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @Bean
    public GitHubClient createGitHubClient(ScrapperConfig scrapperConfig) {
        return new GitHubClient(scrapperConfig.github());
    }
}
