package backend.academy.bot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.command.base.StartCommand;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

public class StartCommandTest extends BaseCommandTest {

    @Autowired
    private StartCommand startCommand;

    @Autowired
    private ScrapperClient scrapperClient;

    @Mock
    private UserStateManager userStateManager;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ScrapperClient scrapperClient() {
            return Mockito.mock(ScrapperClient.class);
        }
    }

    @Test
    @DisplayName("Проверка при вводе первый раз старт")
    void startCommand() {
        Update update = getMockUpdate(5L, "text");
        SendMessage sendMessage = startCommand.handle(update);
        assertEquals(
                "Привет! Используй /help чтобы увидеть все команды",
                sendMessage.getParameters().get("text"));
    }
}
