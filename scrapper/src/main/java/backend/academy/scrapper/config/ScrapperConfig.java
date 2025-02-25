package backend.academy.scrapper.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = true)
public record ScrapperConfig(GithubCredentials github, StackOverflowCredentials stackOverflow) {

    public record GithubCredentials(@NotEmpty String githubToken, @NotEmpty String githubUrl) {
    }

    public record StackOverflowCredentials(@NotEmpty String key, @NotEmpty String accessToken,
                                           @NotEmpty String stackOverFlowUrl) {
    }

}
