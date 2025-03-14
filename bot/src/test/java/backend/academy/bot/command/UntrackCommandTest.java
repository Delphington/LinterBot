package backend.academy.bot.command;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.command.base.UntrackCommand;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.net.URI;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

class UntrackCommandTest extends BaseCommandTest {

    @Autowired
    private UntrackCommand untrackCommand;

    @Autowired
    private ScrapperClient scrapperClient;

    @Autowired
    private ParserMessage parserMessage;

    @Autowired
    private UserStateManager userStateManager;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ScrapperClient scrapperClient() {
            return Mockito.mock(ScrapperClient.class);
        }

        @Bean
        public UserStateManager userStateManager() {
            return Mockito.mock(UserStateManager.class);
        }

        @Bean
        public ParserMessage parserMessage() {
            return Mockito.mock(ParserMessage.class);
        }
    }

    @Test
    @DisplayName("Успешное удаление ссылки")
    @SneakyThrows
    void handleCorrectUrlShouldReturnSuccessResponse() {
        // Arrange
        String commandMessage = "/untrack https://github.com/Delphington";
        Update update = getMockUpdate(2L, commandMessage);

        URI uri = URI.create("https://github.com/Delphington");
        LinkResponse linkResponse = new LinkResponse(5L, uri, List.of(), List.of());

        when(parserMessage.parseUrl(commandMessage)).thenReturn(uri);
        when(scrapperClient.untrackLink(eq(2L), any(RemoveLinkRequest.class))).thenReturn(linkResponse);

        // Act
        SendMessage sendMessage = untrackCommand.handle(update);

        // Assert
        assertEquals(
                "Ссылка удаленна https://github.com/Delphington",
                sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Не корректный ввод URL для удаления")
    @SneakyThrows
    void handleIncorrectUrl() {
        // Arrange
        String commandMessage = "/untrack http://invalidurl";
        Update update = getMockUpdate(2L, commandMessage);

        when(parserMessage.parseUrl(commandMessage))
                .thenThrow(
                        new InvalidInputFormatException("Некорректный URL. Используйте URL в формате /untrack <link>"));

        // Act
        SendMessage sendMessage = untrackCommand.handle(update);

        // Assert
        assertEquals(
                "Некорректный URL. Используйте URL в формате /untrack <link>",
                sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Удаление ссылки, которой не существует")
    @SneakyThrows
    void handleLinkNotFound() {
        // Arrange
        String commandMessage = "/untrack https://github.com/Delphington";
        Update update = getMockUpdate(2L, commandMessage);

        URI uri = URI.create("https://github.com/Delphingtond");

        when(parserMessage.parseUrl(commandMessage)).thenReturn(uri);
        when(scrapperClient.untrackLink(2L, new RemoveLinkRequest(uri)))
                .thenThrow(new ResponseException("Ссылка не найдена"));

        // Act
        SendMessage sendMessage = untrackCommand.handle(update);

        // Assert
        assertEquals("Ссылка не найдена", sendMessage.getParameters().get("text"));
    }
}
