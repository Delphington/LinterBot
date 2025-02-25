package backend.academy.scrapper.api.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {
    public static String sanitize(Long id) {
        return String.valueOf(id).replace("\r", "").replace("\n", "");
    }
}
