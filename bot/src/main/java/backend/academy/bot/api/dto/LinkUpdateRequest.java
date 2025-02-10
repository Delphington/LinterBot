package backend.academy.bot.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

public record LinkUpdateRequest(

    @NotNull(message = "id не может быть null")
    @Positive(message = "id может принимать только положительные значения")
    Long id,

    @NotNull(message = "URL не может быть null")
    //@URL(message = "Не Некорректный URL") Попозже сделать кастомную валидацию
    URI url,

    @NotBlank(message = "Описание не может быть пустым")
    String description,

    @NotNull(message = "Список ID чатов не может быть null")
    List<Long> tgChatIds
) {
}
