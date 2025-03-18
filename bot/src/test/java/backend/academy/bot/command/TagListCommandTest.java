package backend.academy.bot.command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.command.tag.TagListCommand;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

public class TagListCommandTest extends BaseCommandTest {

    @Autowired
    private TagListCommand tagListCommand;

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
    @DisplayName("Некорректный ввод команды с лишними аргументами")
    void handleInvalidTagListInputWithExtraArguments() {
        // Arrange
        Long chatId = 5L;
        String invalidTagListMessage = "/taglist extraArgument";
        Update update = getMockUpdate(chatId, invalidTagListMessage);

        // Метод parseMessageTagList выбрасывает исключение при наличии лишних аргументов
        doThrow(new InvalidInputFormatException("Некорректный формат строки. Ожидается: /taglist"))
                .when(parserMessage)
                .parseMessageTagList(invalidTagListMessage.trim());

        // Act
        SendMessage sendMessage = tagListCommand.handle(update);

        // Assert
        assertEquals(
                "Некорректный формат строки. Ожидается: /taglist",
                sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Некорректный ввод команды с пустым сообщением")
    void handleInvalidTagListInputWithEmptyMessage() {
        // Arrange
        Long chatId = 5L;
        String emptyMessage = "";
        Update update = getMockUpdate(chatId, emptyMessage);

        // Метод parseMessageTagList выбрасывает исключение при пустом сообщении
        doThrow(new InvalidInputFormatException("Некорректный формат строки. Ожидается: /taglist"))
                .when(parserMessage)
                .parseMessageTagList(emptyMessage.trim());

        // Act
        SendMessage sendMessage = tagListCommand.handle(update);

        // Assert
        assertEquals(
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
}
