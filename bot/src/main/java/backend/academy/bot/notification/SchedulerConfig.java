package backend.academy.bot.notification;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalTime;

@Configuration
public class SchedulerConfig {

    @Bean
    public String dailyDigestCron(NotificationProperties properties) {
        LocalTime time = properties.getParsedDigestTime();
        return String.format("0 %d %d * * *", time.getMinute(), time.getHour());
    }
}
