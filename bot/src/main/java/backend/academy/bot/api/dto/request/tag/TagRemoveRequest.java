package backend.academy.bot.api.dto.request.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.net.URI;

public record TagRemoveRequest(
        @NotBlank @Size(max = 50, message = "Длина тега не должна превышать 50 символов") String tag,
        @NotNull(message = "URL не может быть пустым") URI uri) {}
