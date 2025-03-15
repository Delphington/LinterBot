package backend.academy.scrapper.dto.response;

import java.util.List;

public record TagListResponse(
    List<String> tags
) {
}
