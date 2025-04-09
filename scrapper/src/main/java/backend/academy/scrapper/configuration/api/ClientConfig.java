package backend.academy.scrapper.configuration.api;

import backend.academy.scrapper.configuration.ScrapperConfig;
import backend.academy.scrapper.tracker.client.GitHubClient;
import backend.academy.scrapper.tracker.client.StackOverFlowClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @Bean
    public GitHubClient createGitHubClient(ScrapperConfig scrapperConfig) {
        return new GitHubClient(scrapperConfig.github());
    }

    @Bean
    public StackOverFlowClient createStackOverFlowClient(ScrapperConfig scrapperConfig) {
        return new StackOverFlowClient(scrapperConfig.stackOverflow());
    }
}
