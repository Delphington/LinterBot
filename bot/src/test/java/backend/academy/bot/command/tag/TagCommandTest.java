package backend.academy.bot.command.tag;

import static org.mockito.Mockito.when;

import backend.academy.bot.api.dto.request.tag.TagLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.tag.ScrapperTagClient;
import backend.academy.bot.command.TestUtils;
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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TagCommandTest implements TestUtils {

    @Mock
    private ScrapperTagClient scrapperTagClient;

    @Mock
    private ParserMessage parserMessage;

    private TagCommand tagCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tagCommand = new TagCommand(scrapperTagClient, parserMessage);
    }

    @DisplayName("Проверка наименования команды")
    @Test
    void testCommandTrack() {
        Assertions.assertEquals("/tag", tagCommand.command());
    }

    @DisplayName("Проверка описания")
    @Test
    void testCommandDescription() {
        Assertions.assertEquals("Позволяет выводить ссылки по тегам", tagCommand.description());
    }

    private static final Long USER_ID = 14141L;

    @Test
    @DisplayName("Корректный ввод тега и получение списка ссылок")
    void handleValidTagInput() {
        // Arrange
        String tagMessage = "/tag tag1";
        Update update = getMockUpdate(USER_ID, tagMessage);

        String tag = "tag1";
        List<LinkResponse> links = List.of(
                new LinkResponse(1L, URI.create("https://github.com/"), List.of("tag1"), List.of()),
                new LinkResponse(2L, URI.create("https://example.com/"), List.of("tag1"), List.of()));
        ListLinksResponse listLinksResponse = new ListLinksResponse(links, links.size());

        when(parserMessage.parseMessageTag(tagMessage.trim())).thenReturn(tag);
        when(scrapperTagClient.getListLinksByTag(USER_ID, new TagLinkRequest(tag)))
                .thenReturn(listLinksResponse);

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
        String invalidTagMessage = "/tag";
        Update update = getMockUpdate(USER_ID, invalidTagMessage);

        when(parserMessage.parseMessageTag(invalidTagMessage))
                .thenThrow(new InvalidInputFormatException("Тег не может быть пустым"));
        // Act
        SendMessage sendMessage = tagCommand.handle(update);

        // Assert
        Assertions.assertEquals(
                "Тег не может быть пустым", sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Ошибка при получении списка ссылок из базы данных")
    void handleDatabaseError() {
        // Arrange
        String tagMessage = "/tag tag1";
        Update update = getMockUpdate(USER_ID, tagMessage);

        String tag = "tag1";

        when(parserMessage.parseMessageTag(tagMessage.trim())).thenReturn(tag);
        when(scrapperTagClient.getListLinksByTag(USER_ID, new TagLinkRequest(tag)))
                .thenThrow(new ResponseException("Ошибка базы данных"));

        // Act
        SendMessage sendMessage = tagCommand.handle(update);

        // Assert
        String expectedMessage = "С тегом: tag1\nОшибка! попробуй еще раз";
        Assertions.assertEquals(expectedMessage, sendMessage.getParameters().get("text"));
    }
}
