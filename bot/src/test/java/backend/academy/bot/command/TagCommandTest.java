package backend.academy.bot.command;

import static org.mockito.Mockito.when;

import backend.academy.bot.api.dto.request.tag.TagLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.command.tag.TagCommand;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

public class TagCommandTest extends BaseCommandTest {

    @Autowired
    private TagCommand tagCommand;

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
    @DisplayName("Корректный ввод тега и получение списка ссылок")
    void handleValidTagInput() {
        // Arrange
        Long chatId = 5L;
        String tagMessage = "/tag tag1";
        Update update = getMockUpdate(chatId, tagMessage);

        String tag = "tag1";
        List<LinkResponse> links = List.of(
                new LinkResponse(1L, URI.create("https://github.com/"), List.of("tag1"), List.of()),
                new LinkResponse(2L, URI.create("https://example.com/"), List.of("tag1"), List.of()));
        ListLinksResponse listLinksResponse = new ListLinksResponse(links, links.size());

        when(parserMessage.parseMessageTag(tagMessage.trim())).thenReturn(tag);
        when(scrapperClient.getListLinksByTag(chatId, new TagLinkRequest(tag))).thenReturn(listLinksResponse);

        // Act
        SendMessage sendMessage = tagCommand.handle(update);

        // Assert
        String expectedMessage =
                "С тегом: tag1\nОтслеживаемые ссылки:\n1) URL:https://github.com/\n2) URL:https://example.com/\n";
        Assertions.assertEquals(expectedMessage, sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Некорректный ввод тега")
    void handleInvalidTagInput() {
        // Arrange
        Long chatId = 5L;
        String invalidTagMessage = "/tag ";
        Update update = getMockUpdate(chatId, invalidTagMessage);

        when(parserMessage.parseMessageTag(invalidTagMessage.trim()))
                .thenThrow(new InvalidInputFormatException("Тег не может быть пустым"));

        // Act
        SendMessage sendMessage = tagCommand.handle(update);

        // Assert
        Assertions.assertEquals(
                "Тег не может быть пустым", sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Получение пустого списка ссылок по тегу")
    void handleEmptyLinksList() {
        String tagMessage = "/tag tag1";
        String tag = "tag1";
        when(parserMessage.parseMessageTag(tagMessage.trim())).thenReturn(tag);
    }

    @Test
    @DisplayName("Ошибка при получении списка ссылок из базы данных")
    void handleDatabaseError() {
        // Arrange
        Long chatId = 5L;
        String tagMessage = "/tag tag1";
        Update update = getMockUpdate(chatId, tagMessage);

        String tag = "tag1";

        when(parserMessage.parseMessageTag(tagMessage.trim())).thenReturn(tag);
        when(scrapperClient.getListLinksByTag(chatId, new TagLinkRequest(tag)))
                .thenThrow(new ResponseException("Ошибка базы данных"));

        // Act
        SendMessage sendMessage = tagCommand.handle(update);

        // Assert
        String expectedMessage = "С тегом: tag1\nОшибка! попробуй еще раз";
        Assertions.assertEquals(expectedMessage, sendMessage.getParameters().get("text"));
    }
}
