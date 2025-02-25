package backend.academy.scrapper.api.dto.response;

import java.util.List;

public record ListLinksResponse(List<LinkResponse> links, Integer size) {}
