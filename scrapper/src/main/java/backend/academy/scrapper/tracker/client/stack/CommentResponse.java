package backend.academy.scrapper.tracker.client.stack;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;

public record CommentResponse(
    @JsonProperty("items") List<Comment> items
) {
    public record Comment(
        @JsonProperty("owner")
        Owner owner,
        @JsonProperty("creation_date")
        OffsetDateTime createdAt,
        @JsonProperty("body")
        String text
    ) {
        // Конструктор для обрезки текста
        public Comment {
            if (text != null && text.length() > 200) {
                text = text.substring(0, 200);
            }
        }
    }

    public record Owner(
        @JsonProperty("display_name") String name
    ) {
    }
}

