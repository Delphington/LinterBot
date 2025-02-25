package backend.academy.scrapper.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;

// https://api.stackexchange.com/2.3/questions/77847901?order=desc&sort=activity&site=stackoverflow&filter=withbody
public record StackOverFlowResponse(List<ItemResponse> items) {
    public record ItemResponse(
            @JsonProperty("question_id") long id,
            String title,
            @JsonProperty("is_answered") boolean isAnswered,
            @JsonProperty("answer_count") long answerCount,
            @JsonProperty("last_activity_date") OffsetDateTime lastActivityDate,
            @JsonProperty("creation_date") OffsetDateTime creationDate) {}
}
