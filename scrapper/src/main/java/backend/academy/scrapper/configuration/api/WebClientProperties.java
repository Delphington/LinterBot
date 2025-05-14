package backend.academy.scrapper.configuration.api;

import jakarta.validation.constraints.Positive;
import java.time.Duration;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "webclient.timeouts")
@Getter
public class WebClientProperties {
    // Дефолтное заполнение
    @Positive
    private Duration connectTimeout = Duration.ofSeconds(5);

    @Positive
    private Duration responseTimeout = Duration.ofSeconds(5);

    @Positive
    private Duration globalTimeout = Duration.ofSeconds(15);
}
