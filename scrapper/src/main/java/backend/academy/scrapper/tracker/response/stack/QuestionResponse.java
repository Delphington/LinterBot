package backend.academy.scrapper.tracker.response.stack;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;

public record QuestionResponse(List<QuestionItem> items) {
    public record QuestionItem(
            @JsonProperty("last_activity_date") OffsetDateTime updatedAt, @JsonProperty("title") String title) {}
}
