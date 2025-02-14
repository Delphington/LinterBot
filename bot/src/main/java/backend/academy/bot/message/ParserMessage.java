package backend.academy.bot.message;

import backend.academy.bot.command.UserState;
import backend.academy.bot.exception.InvalidInputFormatException;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ParserMessage {

    private final String URL_REGEX = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$";
    private final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);
    private final String[] ALLOWED_DOMAINS = {"github.com", "stackoverflow.com"};


    public URI isValidateTrackInput(String input, UserState userState) {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidInputFormatException("Входная строка не может быть пустой");
        }

        // Разделяем строку на части по пробелам
        String[] parts = input.trim().split("\\s+", 2);

        //пользователь прислал просто ссылку после команды /track
        if (parts.length == 1 && userState == UserState.WAITING_URL && !parts[0].equals("/track")) {
            URI uri = isValidateInputUrl(parts[0]);
            return uri;
        }

        //пользователь прислал /track <URL>
        if (parts.length == 2 && parts[0].equals("/track")) {
            URI uri = isValidateInputUrl(parts[1]);
            return uri;
        }

        throw new InvalidInputFormatException("Отправьте ссылку или же повторите сообщения в таком формате: /track <URL>");
    }

    public URI isValidateInputUrl(String url) {
        if (!isValidUrl(url)) {
            throw new InvalidInputFormatException("Введите корректный URL\nВаш URL: " + url);
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


    public List<String> getAdditionalAttribute(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidInputFormatException("Входная строка не может быть пустой");
        }
        String[] parts = input.trim().split("\\s+");

        return new ArrayList<>(Arrays.asList(parts));

    }


    //Легаси
    public URI parseUrl(String input) {
        return null;
    }

}
