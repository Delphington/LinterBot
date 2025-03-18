package backend.academy.bot.command;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import backend.academy.bot.api.dto.request.tag.TagRemoveRequest;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.command.tag.UnTagCommand;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.net.URI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

public class UnTagCommandTest extends BaseCommandTest {

    @Autowired
    private UnTagCommand unTagCommand;

    @Autowired
    private ScrapperClient scrapperClient;

    @Autowired
    private ParserMessage parserMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ScrapperClient scrapperClient() {
            return Mockito.mock(ScrapperClient.class);
        }

        @Bean
        public ParserMessage parserMessage() {
            return Mockito.mock(ParserMessage.class);
        }
    }

    @Test
    @DisplayName("Некорректный формат команды")
    void handleInvalidUnTagInput() {
        // Arrange
        Long chatId = 5L;
        String invalidUnTagMessage = "/untag";
        Update update = getMockUpdate(chatId, invalidUnTagMessage);

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
        Long chatId = 5L;
        String unTagMessage = "/untag tag1 https://example.com";
        Update update = getMockUpdate(chatId, unTagMessage);

        TagRemoveRequest tagRemoveRequest = new TagRemoveRequest("tag1", URI.create("https://example.com"));

        when(parserMessage.parseMessageUnTag(unTagMessage)).thenReturn(tagRemoveRequest);
        when(scrapperClient.removeTag(chatId, tagRemoveRequest))
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
        Long chatId = 5L;
        String invalidUrlMessage = "/untag tag1 invalidUrl";
        Update update = getMockUpdate(chatId, invalidUrlMessage);

        doThrow(new InvalidInputFormatException("Некорректный URL"))
                .when(parserMessage)
                .parseMessageUnTag(invalidUrlMessage);

        // Act
        SendMessage sendMessage = unTagCommand.handle(update);

        // Assert
        Assertions.assertEquals("Некорректный URL", sendMessage.getParameters().get("text"));
    }
}
