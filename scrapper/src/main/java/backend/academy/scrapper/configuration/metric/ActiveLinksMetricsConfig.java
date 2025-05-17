package backend.academy.scrapper.configuration.metric;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ActiveLinksMetricsConfig {

    private final AtomicInteger githubProcessedLinks = new AtomicInteger(0);
    private final AtomicInteger stackoverflowProcessedLinks = new AtomicInteger(0);

    @Bean
    public AtomicInteger githubProcessedLinksCounter() {
        return githubProcessedLinks;
    }

    @Bean
    public AtomicInteger stackoverflowProcessedLinksCounter() {
        return stackoverflowProcessedLinks;
    }

    @Bean
    public Gauge githubProcessedLinksGauge(MeterRegistry registry,
                                           AtomicInteger githubProcessedLinksCounter) {
        return Gauge.builder("scrapper.links.processed.github",
                githubProcessedLinksCounter::get)
            .register(registry);
    }

    @Bean
    public Gauge stackoverflowProcessedLinksGauge(MeterRegistry registry,
                                                  AtomicInteger stackoverflowProcessedLinksCounter) {
        return Gauge.builder("scrapper.links.processed.stackoverflow",
                stackoverflowProcessedLinksCounter::get)
            .register(registry);
    }
}
