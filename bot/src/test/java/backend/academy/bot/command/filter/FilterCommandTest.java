package backend.academy.bot.command.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.api.dto.request.filter.FilterRequest;
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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FilterCommandTest implements TestUtils {

    @Mock
    private ScrapperFilterClient scrapperFilterClient;

    @Mock
    private ParserMessage parserMessage;

    private FilterCommand filterCommand;

    private static final Long USER_ID = 6758392L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filterCommand = new FilterCommand(scrapperFilterClient, parserMessage);
    }

    @DisplayName("Проверка наименования команды")
    @Test
    void testCommandTrack() {
        Assertions.assertEquals("/filter", filterCommand.command());
    }

    @DisplayName("Проверка описания")
    @Test
    void testCommandDescription() {
        Assertions.assertEquals("Позволяет добавить фильтрацию на получение уведомлений", filterCommand.description());
    }

    private final String VALID_COMMAND = "/filter important";
    private final String INVALID_COMMAND = "/filter";

    @Test
    @DisplayName("Успешное добавление фильтра")
    void handle_shouldSuccessfullyAddFilter() {
        // Arrange
        Update update = getMockUpdate(USER_ID, VALID_COMMAND);
        String expectedErrorMsg = "Некорректный формат ввода. Ожидается: /filter filterName";

        when(parserMessage.parseMessageFilter(VALID_COMMAND, expectedErrorMsg)).thenReturn("important");

        // Act
        SendMessage result = filterCommand.handle(update);

        // Assert
        Assertions.assertEquals(USER_ID, result.getParameters().get("chat_id"));
        Assertions.assertEquals(
                "Фильтр успешно добавлен", result.getParameters().get("text"));
        verify(scrapperFilterClient).createFilter(USER_ID, new FilterRequest("important"));
    }

    @Test
    @DisplayName("Обработка некорректного ввода")
    void handle_shouldHandleInvalidInput() {
        // Arrange
        Update update = getMockUpdate(USER_ID, INVALID_COMMAND);
        String expectedErrorMsg = "Некорректный формат ввода. Ожидается: /filter filterName";
        when(parserMessage.parseMessageFilter(INVALID_COMMAND, expectedErrorMsg))
                .thenThrow(new InvalidInputFormatException("Ошибка формата"));

        // Act
        SendMessage result = filterCommand.handle(update);

        // Assert
        Assertions.assertEquals(USER_ID, result.getParameters().get("chat_id"));
        Assertions.assertEquals("Ошибка формата", result.getParameters().get("text"));
    }

    @Test
    @DisplayName("Обработка существующего фильтра")
    void handle_shouldHandleExistingFilter() {
        // Arrange
        Update update = getMockUpdate(USER_ID, VALID_COMMAND);
        String expectedErrorMsg = "Некорректный формат ввода. Ожидается: /filter filterName";
        when(parserMessage.parseMessageFilter(VALID_COMMAND, expectedErrorMsg)).thenReturn("important");
        when(scrapperFilterClient.createFilter(anyLong(), any())).thenThrow(new ResponseException("Фильтр существует"));

        // Act
        SendMessage result = filterCommand.handle(update);

        // Assert
        Assertions.assertEquals(USER_ID, result.getParameters().get("chat_id"));
        Assertions.assertEquals(
                "Ошибка: такой фильтр уже существует", result.getParameters().get("text"));
    }
}
