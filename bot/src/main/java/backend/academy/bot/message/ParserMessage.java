package backend.academy.bot.message;

import backend.academy.bot.exception.InvalidInputFormatException;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ParserMessage {

    private final String URL_REGEX = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$";
    private final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);
    private final String[] ALLOWED_DOMAINS = {"github.com", "stackoverflow.com"};

    public URI parseUrl(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidInputFormatException("Входная строка не может быть пустой.");
        }

        // Разделяем строку на части по пробелам
        String[] parts = input.trim().split("\\s+", 2);

        // Проверяем, что строка начинается с "/track" и содержит URL
        if (parts.length != 2 || !parts[0].equals("/track")) {
            throw new InvalidInputFormatException("Некорректный формат строки. Ожидается: /track <URL>");
        }

        String url = parts[1];

        if (!isValidUrl(url)) {
            throw new InvalidInputFormatException("Некорректный URL: " + url);
        }

        if (!isAllowedDomain(url)) {
            throw new InvalidInputFormatException("Такой URL не поддерживается: "
                                                  + url + "\n бот поддерживает github.com stackOverflow.com");
        }

        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new InvalidInputFormatException("Некорректное преобразования в uri: " + url);
        }
        return uri;
    }

    private boolean isValidUrl(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        return matcher.matches();
    }


    private boolean isAllowedDomain(String url) {
        for (String domain : ALLOWED_DOMAINS) {
            if (url.contains(domain)) {
                return true;
            }
        }
        return false;
    }
}
