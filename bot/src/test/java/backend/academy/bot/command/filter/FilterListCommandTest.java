package backend.academy.bot.command.filter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import backend.academy.bot.api.dto.response.filter.FilterListResponse;
import backend.academy.bot.api.dto.response.filter.FilterResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.filter.ScrapperFilterClient;
import backend.academy.bot.command.TestUtils;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FilterListCommandTest implements TestUtils {

    @Mock
    private ScrapperFilterClient scrapperFilterClient;

    @Mock
    private ParserMessage parserMessage;

    private FilterListCommand filterListCommand;

    private static final Long USER_ID = 6758392L;

    @BeforeEach
    void setUp() {
        filterListCommand = new FilterListCommand(scrapperFilterClient, parserMessage);
    }

    @DisplayName("Проверка наименования команды")
    @Test
    void testCommandTrack() {
        Assertions.assertEquals("/filterlist", filterListCommand.command());
    }

    @DisplayName("Проверка описания")
    @Test
    void testCommandDescription() {
        Assertions.assertEquals("Выводи все фильтры", filterListCommand.description());
    }

    @DisplayName("Успешное получение списка фильтров")
    @Test
    void handle_SuccessfulFilterList() throws ResponseException, InvalidInputFormatException {
        // Arrange
        Update update = getMockUpdate(USER_ID, "/filterlist");
        List<FilterResponse> filters = List.of(new FilterResponse(1L, "filter1"), new FilterResponse(2L, "filter2"));
        FilterListResponse response = new FilterListResponse(filters);

        when(scrapperFilterClient.getFilterList(USER_ID)).thenReturn(response);

        // Act
        SendMessage result = filterListCommand.handle(update);

        // Assert
        Assertions.assertEquals(USER_ID, result.getParameters().get("chat_id"));
        String expectedMessage = "Фильтры blackList:\n1) filter1\n2) filter2\n";
        Assertions.assertEquals(expectedMessage, result.getParameters().get("text"));
    }

    @DisplayName("Обработка ошибки парсинга сообщения")
    @Test
    void handle_InvalidInputFormat() throws InvalidInputFormatException {
        // Arrange
        Update update = getMockUpdate(USER_ID, "/filterlist Invalid");
        doThrow(new InvalidInputFormatException("Неверный формат"))
                .when(parserMessage)
                .parseMessageFilterList(anyString());

        // Act
        SendMessage result = filterListCommand.handle(update);

        // Assert
        Assertions.assertEquals(USER_ID, result.getParameters().get("chat_id"));
        Assertions.assertTrue(((String) result.getParameters().get("text")).startsWith("Ошибка: "));
    }

    @DisplayName("Обработка ошибки от бэкенда")
    @Test
    void handle_BackendError() throws ResponseException, InvalidInputFormatException {
        // Arrange
        Update update = getMockUpdate(USER_ID, "/filterlist");
        when(scrapperFilterClient.getFilterList(USER_ID)).thenThrow(new ResponseException("Ошибка сервера"));

        // Act
        SendMessage result = filterListCommand.handle(update);

        // Assert
        Assertions.assertEquals(USER_ID, result.getParameters().get("chat_id"));
        Assertions.assertTrue(((String) result.getParameters().get("text")).startsWith("Ошибка: "));
    }
}
