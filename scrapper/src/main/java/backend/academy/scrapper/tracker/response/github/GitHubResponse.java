package backend.academy.scrapper.tracker.response.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record GitHubResponse(
        @JsonProperty("name") String repositoryName, @JsonProperty("updated_at") OffsetDateTime updatedAt) {}
