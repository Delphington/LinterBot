package backend.academy.scrapper.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ScrapperConfig(Github github, StackOverflowCredentials stackOverflow) {

    public record StackOverflowCredentials(@NotEmpty String key, @NotEmpty String accessToken) {
    }

    public record Github(@NotEmpty String githubToken, @NotEmpty String githubUrl) {
    }
}
