package backend.academy.scrapper.tracker.response.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record PullRequestResponse(
    @JsonProperty("title")
    String title,

    @JsonProperty("user")
    User user,

    @JsonProperty("updated_at")
    OffsetDateTime updatedAt,

    @JsonProperty("body")
    String text
) {
    public PullRequestResponse {
        if (text != null && text.length() > 200) {
            text = text.substring(0, 200);
        }
    }

    public record User(
        @JsonProperty("login")
        String login
    ) {
    }
}
