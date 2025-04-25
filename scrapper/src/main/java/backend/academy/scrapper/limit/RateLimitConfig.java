package backend.academy.scrapper.limit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@RequiredArgsConstructor
public class RateLimitConfig {

    private final RateLimitProperties properties;

    @Bean
    public Map<String, Bucket> ipBuckets() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Bandwidth bandwidth() {
        return Bandwidth.classic(properties.capacity(),
            Refill.intervally(
                properties.refillAmount(),
                Duration.ofSeconds(properties.refillSeconds())
            )
        );
    }
}
