package backend.academy.scrapper.limit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bucket4j.rate.limit")
public record RateLimitProperties(
        int capacity,
        int refillAmount,
        int refillSeconds
) {
}
