package backend.academy.bot.api.dto.request;


import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record RemoveLinkRequest(
    @NotNull(message = "URL не может быть пустым")
    URI link
) {
}
