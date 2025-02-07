package backend.academy.scrapper;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.annotation.Validated;
import java.time.Duration;


@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ScrapperConfig(Github github, StackOverflowCredentials stackOverflow, Scheduler scheduler) {

    public record StackOverflowCredentials(@NotEmpty String key, @NotEmpty String accessToken) {
    }

    public record Github(@NotEmpty String githubToken, @NotEmpty String githubUrl) {
    }
    public record Scheduler(boolean enable,  @NotNull Duration interval,
        @NotNull Duration forceCheckDelay) {
    }
}

//---------
//@Validated
//@ConfigurationProperties(prefix = "app.link", ignoreUnknownFields = false)
//public record ScrapperConfig(@NotEmpty  @Value("${app.link.git-hub}") String gitHubUri) {
//   // public record StackOverflowCredentials(@NotEmpty String key, @NotEmpty String accessToken) {}
//}
//
//


//
//@Validated
//@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
//public class ScrapperConfig {
//
//    @NotEmpty
//    private String gitHubUri;
//
//    public String getGitHubUri() {
//        return gitHubUri;
//    }
//
//    public void setGitHubUri(String gitHubUri) {
//        this.gitHubUri = gitHubUri;
//    }
//
//    public ScrapperConfig() {
//    }
//}
//


//@Validated
//@ConfigurationProperties(prefix = "app.link", ignoreUnknownFields = false)
//public record ApplicationConfig(
//    @NotEmpty
//    String gitHubUri,
//    @NotEmpty
//    String stackOverflowUri
//
//) {
//}
