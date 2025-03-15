package backend.academy.scrapper.dto.request.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagLinkRequest(
        @NotBlank @Size(max = 50, message = "Длина тега не должна превышать 50 символов") String tag) {}
