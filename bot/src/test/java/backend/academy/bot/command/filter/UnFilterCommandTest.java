package backend.academy.bot.command.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.api.dto.request.filter.FilterRequest;
import backend.academy.bot.api.dto.response.filter.FilterResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.filter.ScrapperFilterClient;
import backend.academy.bot.command.TestUtils;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UnFilterCommandTest implements TestUtils {
    @Mock
    private ScrapperFilterClient scrapperFilterClient;

    @Mock
    private ParserMessage parserMessage;

    private UnFilterCommand unFilterCommand;

    private static final Long USER_ID = 6758392L;
    private static final String VALID_COMMAND = "/unfilter important";
    private static final String INVALID_COMMAND = "/unfilter";

    @BeforeEach
    void setUp() {
        unFilterCommand = new UnFilterCommand(scrapperFilterClient, parserMessage);
    }

    @Test
    @DisplayName("Проверка наименования команды")
    void testCommandTrack() {
        Assertions.assertEquals("/unfilter", unFilterCommand.command());
    }

    @Test
    @DisplayName("Проверка описания")
    void testCommandDescription() {
        Assertions.assertEquals("Удаление фильтров", unFilterCommand.description());
    }

    @Test
    @DisplayName("Успешное удаление фильтра")
    void handle_shouldSuccessfullyRemoveFilter() {
        // Arrange
        Update update = getMockUpdate(USER_ID, VALID_COMMAND);
        String expectedErrorMsg = "Некорректный формат ввода. Ожидается: /unfilter filterName";

        when(parserMessage.parseMessageFilter(VALID_COMMAND, expectedErrorMsg)).thenReturn("important");

        FilterResponse mockResponse = new FilterResponse(3L, "important");
        when(scrapperFilterClient.deleteFilter(anyLong(), any(FilterRequest.class)))
                .thenReturn(mockResponse);

        // Act
        SendMessage result = unFilterCommand.handle(update);

        // Assert
        Assertions.assertEquals(USER_ID, result.getParameters().get("chat_id"));
        Assertions.assertEquals(
                "фильтр успешно удален: important", result.getParameters().get("text"));
        verify(scrapperFilterClient).deleteFilter(USER_ID, new FilterRequest("important"));
    }

    @Test
    @DisplayName("Обработка некорректного ввода")
    void handle_shouldHandleInvalidInput() {
        // Arrange
        Update update = getMockUpdate(USER_ID, INVALID_COMMAND);
        String expectedErrorMsg = "Некорректный формат ввода. Ожидается: /unfilter filterName";

        when(parserMessage.parseMessageFilter(INVALID_COMMAND, expectedErrorMsg))
                .thenThrow(new InvalidInputFormatException(expectedErrorMsg));

        // Act
        SendMessage result = unFilterCommand.handle(update);

        // Assert
        Assertions.assertEquals(USER_ID, result.getParameters().get("chat_id"));
        Assertions.assertEquals(expectedErrorMsg, result.getParameters().get("text"));
        verify(scrapperFilterClient, never()).deleteFilter(anyLong(), any());
    }

    @Test
    @DisplayName("Обработка ошибки при удалении фильтра")
    void handle_shouldHandleFilterDeletionError() {
        // Arrange
        Update update = getMockUpdate(USER_ID, VALID_COMMAND);
        String expectedErrorMsg = "Некорректный формат ввода. Ожидается: /unfilter filterName";

        when(parserMessage.parseMessageFilter(VALID_COMMAND, expectedErrorMsg)).thenReturn("important");

        when(scrapperFilterClient.deleteFilter(anyLong(), any(FilterRequest.class)))
                .thenThrow(new ResponseException("Фильтр не найден"));

        // Act
        SendMessage result = unFilterCommand.handle(update);

        // Assert
        Assertions.assertEquals(USER_ID, result.getParameters().get("chat_id"));
        Assertions.assertEquals(
                "Ошибка: Фильтр не найден", result.getParameters().get("text"));
        verify(scrapperFilterClient).deleteFilter(USER_ID, new FilterRequest("important"));
    }
}
