package backend.academy.bot.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import java.net.URI;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkUpdateRequest {
    @NotNull @Positive
    private Long id;
    @NotNull @URL
    private URI url;
    @NotBlank
    private String description;
    @NotNull
    private List<Long> tgChatIds;
}
