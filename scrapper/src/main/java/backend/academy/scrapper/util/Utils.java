package backend.academy.scrapper.util;

import lombok.experimental.UtilityClass;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class Utils {
    public static String sanitize(Long id) {
        return String.valueOf(id).replace("\r", "").replace("\n", "");
    }

    public static List<String> getStackTrace(Exception ex) {
        return Arrays.stream(ex.getStackTrace())
            .map(StackTraceElement::toString)
            .toList();
    }
}
