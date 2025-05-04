package backend.academy.bot.notification;

import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class NotificationProperties {

    @Value("${app.notification.mode}")
    private NotificationMode mode;

    @Value("${app.notification.daily-digest-time}")
    private LocalTime digestTime;
}
