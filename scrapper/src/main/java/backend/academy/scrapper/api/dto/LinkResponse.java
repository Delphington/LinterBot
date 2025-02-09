package backend.academy.scrapper.api.dto;

import java.net.URL;
import java.util.List;


public record LinkResponse(
    Long id,
    URL url,
    List<String> tags,
    List<String> filters
) {
}
