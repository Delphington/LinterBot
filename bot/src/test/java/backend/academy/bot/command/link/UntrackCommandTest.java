package backend.academy.bot.command.link;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.command.TestUtils;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import backend.academy.bot.redis.RedisCacheService;
import backend.academy.bot.state.UserState;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.net.URI;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UntrackCommandTest implements TestUtils {

    @Mock
    private ScrapperClient scrapperClient;

    @Mock
    private ParserMessage parserMessage;

    @Mock
    private UserStateManager userStateManager;

    @Mock
    private RedisCacheService redisCacheService;

    private UntrackCommand untrackCommand;

    private final static Long USER_ID = 6758392L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        untrackCommand = new UntrackCommand(scrapperClient, parserMessage, userStateManager, redisCacheService);
    }

    @DisplayName("Проверка наименования команды")
    @Test
    void testCommandTrack() {
        Assertions.assertEquals("/untrack", untrackCommand.command());
    }

    @DisplayName("Проверка описания")
    @Test
    void testCommandDescription() {
        Assertions.assertEquals("Удаляет ссылку для отслеживания", untrackCommand.description());
    }

    @Test
    @DisplayName("Успешное удаление ссылки")
    @SneakyThrows
    void handleCorrectUrlShouldReturnSuccessResponse() {
        // Arrange
        String commandMessage = "/untrack https://github.com/Delphington";
        Update update = getMockUpdate(USER_ID, commandMessage);

        URI uri = URI.create("https://github.com/Delphington");
        LinkResponse linkResponse = new LinkResponse(1L, uri, List.of(), List.of());
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(uri);

        when(parserMessage.parseUrl(commandMessage)).thenReturn(uri);
        when(scrapperClient.untrackLink(USER_ID, removeLinkRequest)).thenReturn(linkResponse);

        // Act
        SendMessage sendMessage = untrackCommand.handle(update);

        // Assert
        Assertions.assertEquals("Ссылка удаленна https://github.com/Delphington", sendMessage.getParameters().get("text"));

        // Verify
        verify(redisCacheService).invalidateCache(USER_ID);
        verify(userStateManager).setUserStatus(USER_ID, UserState.WAITING_COMMAND);
    }



    @Test
    @DisplayName("Не корректный ввод URL для удаления")
    @SneakyThrows
    void handleIncorrectUrl() {
        // Arrange
        String commandMessage = "/untrack";
        Update update = getMockUpdate(USER_ID, commandMessage);

        when(parserMessage.parseUrl(commandMessage))
            .thenThrow(
                new InvalidInputFormatException("Некорректный URL. Используйте URL в формате /untrack <link>"));

        // Act
        SendMessage sendMessage = untrackCommand.handle(update);

        // Assert
        Assertions.assertEquals("Некорректный URL. Используйте URL в формате /untrack <link>", sendMessage.getParameters().get("text"));

        verify(redisCacheService).invalidateCache(USER_ID);
        verify(userStateManager).setUserStatus(USER_ID, UserState.WAITING_COMMAND);
    }

    @Test
    @DisplayName("Удаление ссылки, которой не существует")
    @SneakyThrows
    void handleLinkNotFound() {
        // Arrange
        String commandMessage = "/untrack https://github.com/Delphington";
        Update update = getMockUpdate(USER_ID, commandMessage);

        URI uri = URI.create("https://github.com/Delphington");

        when(parserMessage.parseUrl(commandMessage)).thenReturn(uri);
        when(scrapperClient.untrackLink(eq(USER_ID), any(RemoveLinkRequest.class)))
            .thenThrow(new ResponseException("Ссылка не найдена"));

        // Act
        SendMessage sendMessage = untrackCommand.handle(update);

        // Assert
        Assertions.assertEquals("Ссылка не найдена", sendMessage.getParameters().get("text"));

        verify(redisCacheService).invalidateCache(USER_ID);
        verify(userStateManager).setUserStatus(USER_ID, UserState.WAITING_COMMAND);
    }
}
