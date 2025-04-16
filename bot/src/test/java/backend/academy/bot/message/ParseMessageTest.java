package backend.academy.bot.message;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.bot.api.dto.request.tag.TagRemoveRequest;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.state.UserState;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ParseMessageTest {

    private final ParserMessage parser = new ParserMessage();

    @Test
    @DisplayName("Парсинг URL с командой /track - валидный URL")
    void parseUrl_ValidUrlWithTrackCommand_ReturnsURI() {
        URI result = parser.parseUrl("/track https://github.com/user/repo", UserState.WAITING_URL);
        assertEquals("https://github.com/user/repo", result.toString());
    }

    @Test
    @DisplayName("Парсинг URL в состоянии WAITING_URL - только URL")
    void parseUrl_OnlyUrlInWaitingState_ReturnsURI() {
        URI result = parser.parseUrl("https://stackoverflow.com/questions", UserState.WAITING_URL);
        assertEquals("https://stackoverflow.com/questions", result.toString());
    }

    @Test
    @DisplayName("Парсинг URL - неверный формат URL")
    void parseUrl_InvalidUrlFormat_ThrowsException() {
        assertThrows(
                InvalidInputFormatException.class, () -> parser.parseUrl("/track invalid_url", UserState.WAITING_URL));
    }

    @Test
    @DisplayName("Парсинг URL - неподдерживаемый домен")
    void parseUrl_UnsupportedDomain_ThrowsException() {
        assertThrows(
                InvalidInputFormatException.class,
                () -> parser.parseUrl("/track http://google.com", UserState.WAITING_URL));
    }

    @Test
    @DisplayName("Парсинг команды /untrack - валидный URL")
    void parseUrl_ValidUntrackCommand_ReturnsURI() {
        URI result = parser.parseUrl("/untrack https://github.com/user/repo");
        assertEquals("https://github.com/user/repo", result.toString());
    }

    @Test
    @DisplayName("Парсинг команды /untrack - неверный формат")
    void parseUrl_InvalidUntrackFormat_ThrowsException() {
        assertThrows(InvalidInputFormatException.class, () -> parser.parseUrl("/untrack"));
    }

    @Test
    @DisplayName("Получение дополнительных атрибутов - валидный ввод")
    void getAdditionalAttribute_ValidInput_ReturnsList() {
        List<String> result = parser.getAdditionalAttribute("arg1 arg2 arg3");
        assertEquals(List.of("arg1", "arg2", "arg3"), result);
    }

    @Test
    @DisplayName("Получение дополнительных атрибутов - пустой ввод")
    void getAdditionalAttribute_EmptyInput_ThrowsException() {
        assertThrows(InvalidInputFormatException.class, () -> parser.getAdditionalAttribute(""));
    }

    @Test
    @DisplayName("Парсинг команды /tag - валидный тег")
    void parseMessageTag_ValidCommand_ReturnsTag() {
        String tag = parser.parseMessageTag("/tag mytag");
        assertEquals("mytag", tag);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/tag", "/tag ", "/tag mytag extra"})
    @DisplayName("Парсинг команды /tag - неверные форматы")
    void parseMessageTag_InvalidFormats_ThrowsException(String input) {
        assertThrows(InvalidInputFormatException.class, () -> parser.parseMessageTag(input));
    }

    @Test
    @DisplayName("Парсинг команды /taglist - валидная команда")
    void parseMessageTagList_ValidCommand_NoException() {
        assertDoesNotThrow(() -> parser.parseMessageTagList("/taglist"));
    }

    @Test
    @DisplayName("Парсинг команды /taglist - с аргументами")
    void parseMessageTagList_WithArguments_ThrowsException() {
        assertThrows(InvalidInputFormatException.class, () -> parser.parseMessageTagList("/taglist arg"));
    }

    @Test
    @DisplayName("Парсинг команды /untag - валидный запрос")
    void parseMessageUnTag_ValidCommand_ReturnsRequest() {
        TagRemoveRequest request = parser.parseMessageUnTag("/untag mytag https://github.com");
        assertEquals("mytag", request.tag());
        assertEquals("https://github.com", request.uri().toString());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"/untag", "/untag mytag", "/untag mytag invalid_url", "invalid_cmd mytag https://github.com"})
    @DisplayName("Парсинг команды /untag - неверные форматы")
    void parseMessageUnTag_InvalidFormats_ThrowsException(String input) {
        assertThrows(InvalidInputFormatException.class, () -> parser.parseMessageUnTag(input));
    }

    @Test
    @DisplayName("Парсинг команды /filter - валидный фильтр")
    void parseMessageFilter_ValidCommand_ReturnsFilter() {
        String filter = parser.parseMessageFilter("/filter java", "error");
        assertEquals("java", filter);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/filter", "/filter ", "invalid"})
    @DisplayName("Парсинг команды /filter - неверные форматы")
    void parseMessageFilter_InvalidFormats_ThrowsException(String input) {
        assertThrows(InvalidInputFormatException.class, () -> parser.parseMessageFilter(input, "Custom error"));
    }

    @Test
    @DisplayName("Парсинг команды /filterlist - валидная команда")
    void parseMessageFilterList_ValidCommand_NoException() {
        assertDoesNotThrow(() -> parser.parseMessageFilterList("/filterlist"));
    }

    @Test
    @DisplayName("Парсинг команды /filterlist - с аргументами")
    void parseMessageFilterList_WithArguments_ThrowsException() {
        assertThrows(InvalidInputFormatException.class, () -> parser.parseMessageFilterList("/filterlist arg"));
    }
}
