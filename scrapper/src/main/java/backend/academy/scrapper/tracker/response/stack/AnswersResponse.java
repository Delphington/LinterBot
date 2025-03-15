package backend.academy.scrapper.tracker.response.stack;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;

public record AnswersResponse(List<Answer> items) {
    public record Answer(
            @JsonProperty("owner") Owner owner,
            @JsonProperty("creation_date") OffsetDateTime createdAt,
            @JsonProperty("body") String text) {
        // конструктор для обрезки текста
        public Answer {
            if (text != null && text.length() > 200) {
                text = text.substring(0, 200);
            }
        }
    }

    public record Owner(@JsonProperty("display_name") String name) {}
}
