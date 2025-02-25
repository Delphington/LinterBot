package backend.academy.bot.message;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.state.UserState;
import java.net.URI;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ParserMessageTest {

    private ParserMessage parserMessage;

    @BeforeEach
    void setUp() {
        parserMessage = new ParserMessage();
    }

    @Test
    @DisplayName("Парсинг валидного URL: пользователь отправляет ссылку после команды /track")
    @SneakyThrows
    void testParseUrl_ValidUrlAfterTrackCommand() {
        String input = "/track https://github.com/example";
        UserState userState = UserState.WAITING_URL;
        URI result = parserMessage.parseUrl(input, userState);
        assertEquals(new URI("https://github.com/example"), result);
    }

    @Test
    @DisplayName("Парсинг валидного URL: пользователь отправляет только ссылку")
    @SneakyThrows
    void testParseUrl_ValidUrlOnly() {
        String input = "https://github.com/example";
        UserState userState = UserState.WAITING_URL;
        URI result = parserMessage.parseUrl(input, userState);
        assertEquals(new URI("https://github.com/example"), result);
    }

    @Test
    @DisplayName("Парсинг невалидного URL: некорректный формат ссылки")
    void testParseUrl_InvalidUrlFormat() {
        String input = "/track invalid-url";
        UserState userState = UserState.WAITING_URL;
        assertThrows(InvalidInputFormatException.class, () -> parserMessage.parseUrl(input, userState));
    }

    @Test
    @DisplayName("Парсинг URL: неподдерживаемый домен")
    void testParseUrl_UnsupportedDomain() {
        String input = "/track https://unsupported.com/example";
        UserState userState = UserState.WAITING_URL;
        assertThrows(InvalidInputFormatException.class, () -> parserMessage.parseUrl(input, userState));
    }

    @Test
    @DisplayName("Парсинг URL: пустая строка")
    void testParseUrl_EmptyInput() {
        String input = "";
        UserState userState = UserState.WAITING_URL;
        assertThrows(InvalidInputFormatException.class, () -> parserMessage.parseUrl(input, userState));
    }

    @Test
    @DisplayName("Парсинг URL: команда /untrack с валидным URL")
    @SneakyThrows
    void testParseUrl_UntrackCommandWithValidUrl() {
        String input = "/untrack https://github.com/example";
        URI result = parserMessage.parseUrl(input);
        assertEquals(new URI("https://github.com/example"), result);
    }

    @Test
    @DisplayName("Парсинг URL: команда /untrack с невалидным URL")
    void testParseUrl_UntrackCommandWithInvalidUrl() {
        String input = "/untrack invalid-url";
        assertThrows(InvalidInputFormatException.class, () -> parserMessage.parseUrl(input));
    }

    @Test
    @DisplayName("Парсинг URL: команда /untrack с неподдерживаемым доменом")
    void testParseUrl_UntrackCommandWithUnsupportedDomain() {
        String input = "/untrack https://unsupported.com/example";
        assertThrows(InvalidInputFormatException.class, () -> parserMessage.parseUrl(input));
    }

    @Test
    @DisplayName("Парсинг дополнительных атрибутов: валидная строка")
    void testGetAdditionalAttribute_ValidInput() {
        String input = "attr1 attr2 attr3";
        List<String> result = parserMessage.getAdditionalAttribute(input);
        assertEquals(List.of("attr1", "attr2", "attr3"), result);
    }

    @Test
    @DisplayName("Парсинг дополнительных атрибутов: пустая строка")
    void testGetAdditionalAttribute_EmptyInput() {
        String input = "";
        assertThrows(InvalidInputFormatException.class, () -> parserMessage.getAdditionalAttribute(input));
    }
}
