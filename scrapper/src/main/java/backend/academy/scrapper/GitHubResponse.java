package backend.academy.scrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record GitHubResponse(
    @JsonProperty("id")
    Long repositoryId,
    @JsonProperty("full_name")
    String fullName,
    String description,
    @JsonProperty("created_at")
    OffsetDateTime created,
    @JsonProperty("updated_at")
    OffsetDateTime updated,
    @JsonProperty("pushed_at")
    OffsetDateTime pushed,
    String language,
    Owner owner) {

    public record Owner(
        String login,
        Long id,
        @JsonProperty("avatar_url") String avatarUrl) {
    }

}
