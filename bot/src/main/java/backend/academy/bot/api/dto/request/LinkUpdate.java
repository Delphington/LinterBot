package backend.academy.bot.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

public record LinkUpdate(
        @NotNull(message = "chatId не может быть null")
                @Positive(message = "chatId может принимать только положительные значения")
                Long id,
        @NotNull(message = "URL не может быть null") URI url,
        @NotNull(message = "description не может быть null") @NotBlank(message = "Описание не может быть пустым")
                String description,
        @NotNull(message = "Список ID чатов не может быть null") List<Long> tgChatIds) {}
