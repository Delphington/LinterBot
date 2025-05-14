package backend.academy.bot.api.dto.request.filter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FilterRequest(
        @NotBlank @Size(max = 50, message = "Длина фильтра не должна превышать 50 символов") String filter) {}
