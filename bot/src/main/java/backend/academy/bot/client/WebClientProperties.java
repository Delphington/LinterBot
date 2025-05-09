package backend.academy.bot.client;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
@Getter
@Setter
public class WebClientProperties {

    @Value("${app.webclient.timeouts.connect-timeout}")
    @NotNull
    @Positive
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration connectTimeout;

    @Value("${app.webclient.timeouts.response-timeout}")
    @NotNull
    @Positive
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration responseTimeout;

    @Value("${app.webclient.timeouts.global-timeout}")
    @NotNull
    @Positive
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration globalTimeout;
}
