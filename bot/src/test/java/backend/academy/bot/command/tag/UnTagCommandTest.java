package backend.academy.bot.command.tag;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import backend.academy.bot.api.dto.request.tag.TagRemoveRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.command.TestUtils;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import backend.academy.bot.redis.RedisCacheService;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UnTagCommandTest implements TestUtils {

    private UnTagCommand unTagCommand;

    @Mock
    private ScrapperClient scrapperClient;

    @Mock
    private ParserMessage parserMessage;

    @Mock
    private RedisCacheService redisCacheService;

    private final static Long USER_ID = 245151L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        unTagCommand = new UnTagCommand(scrapperClient, parserMessage, redisCacheService);
    }

    @Test
    @DisplayName("Проверка команды")
    void shouldReturnCorrectCommand() {
        Assertions.assertEquals("/untag", unTagCommand.command());
    }

    @Test
    @DisplayName("Проверка описания")
    void shouldReturnCorrectDescription() {
        Assertions.assertEquals("Удаление тега у ссылок", unTagCommand.description());
    }

    @Test
    @DisplayName("Некорректный формат команды")
    void handleInvalidUnTagInput() {
        // Arrange
        String invalidUnTagMessage = "/untag";
        Update update = getMockUpdate(USER_ID, invalidUnTagMessage);

        doThrow(new InvalidInputFormatException("Некорректный формат команды. Ожидается: /untag <тег> <ссылка>"))
            .when(parserMessage)
            .parseMessageUnTag(invalidUnTagMessage);

        // Act
        SendMessage sendMessage = unTagCommand.handle(update);

        // Assert
        Assertions.assertEquals(
            "Некорректный формат команды. Ожидается: /untag <тег> <ссылка>",
            sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Ошибка при удалении тега")
    void handleUnTagError() {
        // Arrange
        String unTagMessage = "/untag tag1 https://example.com";
        Update update = getMockUpdate(USER_ID, unTagMessage);

        TagRemoveRequest tagRemoveRequest = new TagRemoveRequest("tag1", URI.create("https://example.com"));

        when(parserMessage.parseMessageUnTag(unTagMessage)).thenReturn(tagRemoveRequest);
        when(scrapperClient.removeTag(USER_ID, tagRemoveRequest))
            .thenThrow(new ResponseException("Ошибка при удалении тега"));

        // Act
        SendMessage sendMessage = unTagCommand.handle(update);

        // Assert
        Assertions.assertEquals(
            "Ошибка: Ошибка при удалении тега", sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Некорректный URL в команде")
    void handleInvalidUrlInUnTagCommand() {
        // Arrange
        String invalidUrlMessage = "/untag tag1 invalidUrl";
        Update update = getMockUpdate(USER_ID, invalidUrlMessage);

        doThrow(new InvalidInputFormatException("Некорректный URL"))
            .when(parserMessage)
            .parseMessageUnTag(invalidUrlMessage);

        // Act
        SendMessage sendMessage = unTagCommand.handle(update);

        // Assert
        Assertions.assertEquals("Некорректный URL", sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Успешное удаление тега")
    void handle_shouldSuccessfullyRemoveTag() {
        // Arrange
        String COMMAND_TEXT = "/untag test_tag https://github.com";
        Update update = getMockUpdate(USER_ID, COMMAND_TEXT); // Используем полный текст команды

        TagRemoveRequest tagRemoveRequest = new TagRemoveRequest("test_tag", URI.create("https://github.com"));
        when(parserMessage.parseMessageUnTag(COMMAND_TEXT)).thenReturn(tagRemoveRequest);

        LinkResponse mockResponse = new LinkResponse(
            1L,
            URI.create("https://github.com"),
            List.of("remaining_tag"),
            List.of("filter1")
        );
        when(scrapperClient.removeTag(anyLong(), any(TagRemoveRequest.class))).thenReturn(mockResponse);

        // Act
        SendMessage result = unTagCommand.handle(update);

        // Assert
        String expectedMessage = """
            Теги обновлены:
            Ссылка: https://github.com
            Теги: [remaining_tag]
            Фильтры: [filter1]""";

        Assertions.assertEquals(USER_ID, result.getParameters().get("chat_id"));
        Assertions.assertEquals(expectedMessage, result.getParameters().get("text"));

        verify(redisCacheService).invalidateCache(USER_ID);
        verify(scrapperClient).removeTag(USER_ID, tagRemoveRequest);
    }
}
