package backend.academy.bot.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
@Getter
@Setter
public class WebServiceProperties {

    @Value("${app.link.scrapper-uri}")
    @NotNull
    @NotBlank
    private String scrapperUri;
}
