package backend.academy.bot.client;

import jakarta.validation.constraints.Positive;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "webclient.timeouts")
@Getter
@Setter
public class WebClientProperties {
    // Дефолтное заполнение
    @Positive
    private Duration connectTimeout = Duration.ofSeconds(5);

    @Positive
    private Duration responseTimeout = Duration.ofSeconds(5);

    @Positive
    private Duration globalTimeout = Duration.ofSeconds(15);

    @Value("${app.link.scrapper-uri}")
    private String baseUrl = "http://localhost:8081";
}
