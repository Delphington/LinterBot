package backend.academy.bot.api;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ApiErrorResponse {
    @NotBlank
    private String description;
    @NotBlank
    private String code;
    @NotBlank
    private String exceptionName;
    @NotBlank
    private String exceptionMessage;
    @NotBlank
    private List<String> stacktrace;
}
