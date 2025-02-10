package backend.academy.scrapper.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "scheduler", ignoreUnknownFields = true)
public record SchedulerConfig(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
}
