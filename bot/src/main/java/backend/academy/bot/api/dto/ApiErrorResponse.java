package backend.academy.bot.api.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ApiErrorResponse(
    @NotBlank(message = "description не может быть пустым")
    String description,

    @NotBlank(message = "code не может быть пустым")
    String code,

    String exceptionName,
    String exceptionMessage,
    List<String> stacktrace
) {
}
