package backend.academy.scrapper.client.tracker;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record UpdateLinkResponse(
    @NotNull
    Long id,
    @NotNull
    URI url,
    @NotBlank
    String description,
   // @NotNull(message = "Список ID чатов не может быть null")
    List<Long> tgChatIds
) {

}
