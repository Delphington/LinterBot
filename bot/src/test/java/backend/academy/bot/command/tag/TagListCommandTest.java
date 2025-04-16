package backend.academy.bot.command.tag;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import backend.academy.bot.api.dto.response.TagListResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TagListCommandTest implements TestUtils {

    private TagListCommand tagListCommand;

    @Mock
    private ScrapperClient scrapperClient;

    @Mock
    private ParserMessage parserMessage;

    private static final Long USER_ID = 245151L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tagListCommand = new TagListCommand(scrapperClient, parserMessage);
    }

    @Test
    @DisplayName("Проверка команды")
    void shouldReturnCorrectCommand() {
        Assertions.assertEquals("/taglist", tagListCommand.command());
    }

    @Test
    @DisplayName("Проверка описания")
    void shouldReturnCorrectDescription() {
        Assertions.assertEquals("Выводит все теги пользователя", tagListCommand.description());
    }

    @Test
    @DisplayName("Некорректный ввод команды с лишними аргументами")
    void handleInvalidTagListInputWithExtraArguments() {
        // Arrange
        String invalidTagListMessage = "/taglist extraArgument";
        Update update = getMockUpdate(USER_ID, invalidTagListMessage);

        // Метод parseMessageTagList выбрасывает исключение при наличии лишних аргументов
        doThrow(new InvalidInputFormatException("Некорректный формат строки. Ожидается: /taglist"))
                .when(parserMessage)
                .parseMessageTagList(invalidTagListMessage.trim());

        // Act
        SendMessage sendMessage = tagListCommand.handle(update);

        // Assert
        Assertions.assertEquals(
                "Некорректный формат строки. Ожидается: /taglist",
                sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Некорректный ввод команды с пустым сообщением")
    void handleInvalidTagListInputWithEmptyMessage() {
        // Arrange
        String emptyMessage = "";
        Update update = getMockUpdate(USER_ID, emptyMessage);

        doThrow(new InvalidInputFormatException("Некорректный формат строки. Ожидается: /taglist"))
                .when(parserMessage)
                .parseMessageTagList(emptyMessage.trim());

        // Act
        SendMessage sendMessage = tagListCommand.handle(update);

        // Assert
        Assertions.assertEquals(
                "Некорректный формат строки. Ожидается: /taglist",
                sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Ошибка при получении списка тегов из базы данных")
    void handleDatabaseError() {
        // Arrange
        Long chatId = 5L;
        String tagListMessage = "/taglist";
        Update update = getMockUpdate(chatId, tagListMessage);

        // Метод parseMessageTagList не выбрасывает исключение для корректного ввода
        when(scrapperClient.getAllListLinksByTag(chatId)).thenThrow(new ResponseException("Ошибка базы данных"));

        // Act
        SendMessage sendMessage = tagListCommand.handle(update);

        // Assert
        Assertions.assertEquals(
                "Ошибка попробуй еще раз", sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Успешное получение списка тегов")
    void handle_shouldReturnTagListSuccessfully() throws Exception {
        // Arrange
        String commandText = "/taglist";
        Update update = getMockUpdate(USER_ID, commandText);

        TagListResponse mockResponse = new TagListResponse(List.of("tag1", "tag2", "tag3"));

        when(scrapperClient.getAllListLinksByTag(anyLong())).thenReturn(mockResponse);

        // Act
        SendMessage result = tagListCommand.handle(update);

        // Assert
        String expectedMessage =
                """
            Ваши теги:
            1) tag1
            2) tag2
            3) tag3
            """
                        .trim();

        Assertions.assertEquals(USER_ID, result.getParameters().get("chat_id"));
        Assertions.assertEquals(
                expectedMessage, result.getParameters().get("text").toString().trim());
    }
}
