package backend.academy.bot.message;

import backend.academy.bot.api.dto.request.tag.TagRemoveRequest;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.state.UserState;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class ParserMessage {

    private static final String URL_REGEX = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);
    private static final String[] ALLOWED_DOMAINS = {"github.com", "stackoverflow.com"};

    public URI parseUrl(String input, UserState userState) {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidInputFormatException("Входная строка не может быть пустой");
        }

        // Разделяем строку на части по пробелам
        String[] parts = input.trim().split("\\s+", 2);

        // пользователь прислал просто ссылку после команды /track
        if (parts.length == 1 && userState == UserState.WAITING_URL && !parts[0].equals("/track")) {
            URI uri = isValidateInputUrl(parts[0]);
            return uri;
        }

        // пользователь прислал /track <URL>
        if (parts.length == 2 && parts[0].equals("/track")) {
            URI uri = isValidateInputUrl(parts[1]);
            return uri;
        }

        throw new InvalidInputFormatException(
                "Отправьте ссылку или же " + "повторите сообщения в таком формате: /track <URL>");
    }

    public URI isValidateInputUrl(String url) {
        if (!isValidUrl(url)) {
            throw new InvalidInputFormatException("Введите корректный URL\nВаш URL: " + url);
        }

        if (!isAllowedDomain(url)) {
            throw new InvalidInputFormatException(
                    "Такой URL не поддерживается: " + url + "\n бот поддерживает github.com stackOverflow.com");
        }

        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new InvalidInputFormatException("Некорректное преобразования в uri: " + url);
        }
        return uri;
    }

    public URI parseUrl(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidInputFormatException("Входная строка не может быть пустой.");
        }

        // Разделяем строку на части по пробелам
        String[] parts = input.trim().split("\\s+", 2);

        // Проверяем, что строка начинается с "/track" и содержит URL
        if (parts.length != 2 || !parts[0].equals("/untrack")) {
            throw new InvalidInputFormatException("Некорректный формат строки. Ожидается: /untrack <URL>");
        }

        String url = parts[1];

        if (!isValidUrl(url)) {
            throw new InvalidInputFormatException("Некорректный URL: " + url);
        }

        if (!isAllowedDomain(url)) {
            throw new InvalidInputFormatException(
                    "Такой URL не поддерживается: " + url + "\n бот поддерживает github.com stackOverflow.com");
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

    public List<String> getAdditionalAttribute(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidInputFormatException("Входная строка не может быть пустой");
        }
        return new ArrayList<>(Arrays.asList(input.trim().split("\\s+")));
    }

    // --- Для парсинга /tag
    public String parseMessageTag(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidInputFormatException("Некорректный формат строки. Ожидается: /tag <название>");
        }

        String[] arr = message.split(" ");
        if (arr.length != 2) {
            throw new InvalidInputFormatException("Некорректный формат строки. Ожидается: /tag <название>");
        } else {
            return arr[1];
        }
    }

    public void parseMessageTagList(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidInputFormatException("Некорректный формат строки. Ожидается: /taglist");
        }
        String[] arr = message.split(" ");
        if (arr.length > 1) {
            throw new InvalidInputFormatException("Некорректный формат строки. Ожидается: /taglist");
        }
    }

    public TagRemoveRequest parseMessageUnTag(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidInputFormatException("1. Некорректный формат строки. Ожидается: /untag name_tag uri");
        }

        String[] arr = message.split(" ");
        if (arr.length != 3) {
            throw new InvalidInputFormatException("2. Некорректный формат строки. Ожидается: /untag name_tag uri");
        }

        if (!"/untag".equals(arr[0])) {
            throw new InvalidInputFormatException("3. Некорректный формат строки. Ожидается: /untag name_tag uri");
        }

        URI uri = isValidateInputUrl(arr[2]);

        return new TagRemoveRequest(arr[1], uri);
    }

    // Для парсинга фильтров
    public String parseMessageFilter(String message, String messageError) {
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidInputFormatException(messageError);
        }
        String[] arr = message.split(" ");
        if (arr.length != 2) {
            throw new InvalidInputFormatException(messageError);
        }

        return arr[1];
    }

    public void parseMessageFilterList(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidInputFormatException("Ошибка. Ожидается: /filterlist");
        }
        String[] arr = message.split(" ");
        if (arr.length != 1) {
            throw new InvalidInputFormatException("Ошибка. Ожидается: /filterlist");
        }
    }
}
