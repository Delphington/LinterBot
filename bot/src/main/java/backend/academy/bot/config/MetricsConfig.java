package backend.academy.bot.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterBinder meterBinder() {
        return registry -> {
            Counter.builder("msg_count")
                .description("Количество сообщений от пользователей")
                .register(registry);
        };
    }
}


