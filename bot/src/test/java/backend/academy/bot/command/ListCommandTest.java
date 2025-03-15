package backend.academy.bot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.command.base.ListCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

public class ListCommandTest extends BaseCommandTest {

    @Autowired
    private ListCommand listCommand;

    @Autowired
    private ScrapperClient scrapperClient;

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
    }

    @Test
    @DisplayName("Тест на отслеживания ссылок, которых нет")
    public void handleEmptyTrackList() {
        Long chatId = 1L;
        Update update = getMockUpdate(chatId, "text");
        when(scrapperClient.getListLink(chatId)).thenReturn(new ListLinksResponse(List.of(), 0));
        SendMessage sendMessage = listCommand.handle(update);
        assertEquals(
                "Никакие ссылки еще не отслеживаются",
                sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Тест на проверку, отслеживаемых ссылок")
    public void handleNotEmptyTrackList() {
        Long chatId = 2L;
        Update update = getMockUpdate(chatId, "text");

        List<LinkResponse> links = List.of(
                new LinkResponse(5L, URI.create("http://github.com"), List.of("tag1"), List.of("filter1")),
                new LinkResponse(6L, URI.create("http://stackoverflow.com"), List.of("tag2"), List.of("filter2")));
        ListLinksResponse response = new ListLinksResponse(links, links.size());

        // Мокируем список ссылок
        when(scrapperClient.getListLink(chatId)).thenReturn(response);

        // Act
        SendMessage sendMessage = listCommand.handle(update);

        // Assert
        String expectedMessage = "Отслеживаемые ссылки:\n" + "1)\n"
                + "URL:http://github.com\n"
                + "tags:[tag1]\n"
                + "filters:[filter1]\n"
                + "2)\n"
                + "URL:http://stackoverflow.com\n"
                + "tags:[tag2]\n"
                + "filters:[filter2]\n";
        assertEquals(expectedMessage, sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Тест на проверку, отслеживаемых ссылок, с ошибкой при получении ссылок")
    public void handleResponseException() {
        Long chatId = 3L;
        Update update = getMockUpdate(chatId, "text");

        when(scrapperClient.getListLink(chatId)).thenThrow(new ResponseException("Ошибка"));

        SendMessage sendMessage = listCommand.handle(update);
        assertEquals("Ошибка при получении ссылок", sendMessage.getParameters().get("text"));
    }
}
