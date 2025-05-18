package backend.academy.scrapper.configuration.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PercentileMetricsConfig {

    @Bean
    public Timer githubScrapeTimer(MeterRegistry registry) {
        return Timer.builder("scrapper.scrape.time")
                .description("Time taken to scrape GitHub links")
                .register(registry);
    }

    @Bean
    public Timer stackoverflowScrapeTimer(MeterRegistry registry) {
        return Timer.builder("scrapper.scrape.time")
                .description("Time taken to scrape StackOverflow links")
                .register(registry);
    }
}
