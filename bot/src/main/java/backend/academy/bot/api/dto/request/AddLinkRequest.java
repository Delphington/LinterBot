package backend.academy.bot.api.dto.request;

import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record AddLinkRequest(
        @NotNull(message = "URL не может быть пустым")
        URI link,
        List<String> tags,
        List<String> filters)
{}
