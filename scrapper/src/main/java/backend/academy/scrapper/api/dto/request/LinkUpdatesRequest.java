package backend.academy.scrapper.api.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record LinkUpdatesRequest(
    @NotNull
    Long id,
    @NotNull
    URI url,
    @NotBlank
    String description,
    @NotNull
    List<Long> tgChatIds
) {

}
