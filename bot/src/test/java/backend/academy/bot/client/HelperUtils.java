package backend.academy.bot.client;

import java.time.Duration;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HelperUtils {

    // Вспомогательный метод для парсинга Duration
    public static Duration parseDuration(String durationStr) {
        if (durationStr.startsWith("PT")) {
            return Duration.parse(durationStr);
        }
        if (durationStr.endsWith("s")) {
            long seconds = Long.parseLong(durationStr.substring(0, durationStr.length() - 1));
            return Duration.ofSeconds(seconds);
        }
        throw new IllegalArgumentException("Invalid duration format: " + durationStr);
    }
}
