package backend.academy.bot.command.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;

import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.command.TestUtils;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StartCommandTest implements TestUtils {

    @Mock
    private ScrapperClient scrapperClient;

    @Mock
    private UserStateManager userStateManager;

    private StartCommand startCommand;

    private static final Long USER_ID = 10231L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        startCommand = new StartCommand(scrapperClient, userStateManager);
    }

    @DisplayName("Проверка наименования команды")
    @Test
    void testCommandTrack() {
        Assertions.assertEquals("/start", startCommand.command());
    }

    @DisplayName("Проверка описания")
    @Test
    void testCommandDescription() {
        Assertions.assertEquals("Начинает работу бота", startCommand.description());
    }

    @Test
    @DisplayName("Проверка при вводе первый раз старт")
    void startCommand() {
        Update update = getMockUpdate(USER_ID, "text");
        SendMessage sendMessage = startCommand.handle(update);
        assertEquals(
                "Привет! Используй /help чтобы увидеть все команды",
                sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Проверка при вводе второй раз старт")
    void startCommandTwoTime() {
        // Arrange
        Update update = getMockUpdate(USER_ID, "/start");
        doThrow(new ResponseException("Ты уже зарегистрировался :)"))
                .when(scrapperClient)
                .registerChat(USER_ID);

        // Act
        SendMessage result = startCommand.handle(update);

        // Assert
        assertEquals("Ты уже зарегистрировался :)", result.getParameters().get("text"));
    }
}
