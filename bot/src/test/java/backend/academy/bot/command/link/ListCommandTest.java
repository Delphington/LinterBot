package backend.academy.bot.command.link;

import static org.mockito.Mockito.*;

import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.redis.RedisCacheService;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
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
public class ListCommandTest {

    private ListCommand listCommand;

    @Mock
    private ScrapperClient scrapperClient;

    @Mock
    private UserStateManager userStateManager;

    @Mock
    private RedisCacheService redisCacheService;

    private static final Long USER_ID = 6758392L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listCommand = new ListCommand(scrapperClient, userStateManager, redisCacheService);
    }

    @DisplayName("Проверка наименования команды")
    @Test
    void testCommandTrack() {
        Assertions.assertEquals("/list", listCommand.command());
    }

    @DisplayName("Проверка описания")
    @Test
    void testCommandDescription() {
        Assertions.assertEquals("Выводит список отслеживаемых ссылок", listCommand.description());
    }

    @Test
    @DisplayName("Тест на отслеживания ссылок, которых нет")
    public void handleEmptyTrackList() {
        Update update = getMockUpdate(USER_ID);
        when(scrapperClient.getListLink(USER_ID)).thenReturn(new ListLinksResponse(List.of(), 0));
        SendMessage sendMessage = listCommand.handle(update);
        Assertions.assertEquals(
                "Никакие ссылки не отслеживаются", sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Тест на проверку, отслеживаемых ссылок")
    public void handleNotEmptyTrackList() {
        Update update = getMockUpdate(USER_ID);

        List<LinkResponse> links = List.of(
                new LinkResponse(5L, URI.create("http://github.com"), List.of("tag1"), List.of("filter1")),
                new LinkResponse(6L, URI.create("http://stackoverflow.com"), List.of("tag2"), List.of("filter2")));
        ListLinksResponse response = new ListLinksResponse(links, links.size());

        when(scrapperClient.getListLink(USER_ID)).thenReturn(response);

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
        Assertions.assertEquals(expectedMessage, sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Тест на проверку, отслеживаемых ссылок, с ошибкой при получении ссылок")
    public void handleResponseException() {
        Update update = getMockUpdate(USER_ID);

        when(scrapperClient.getListLink(USER_ID)).thenThrow(new ResponseException("Ошибка"));

        SendMessage sendMessage = listCommand.handle(update);
        Assertions.assertEquals("Ошибка", sendMessage.getParameters().get("text"));
    }

    private Update getMockUpdate(Long id) {
        Update update = mock(Update.class);
        Chat chat = mock(Chat.class);
        when(chat.id()).thenReturn(id);
        Message message = mock(Message.class);
        when(message.chat()).thenReturn(chat);
        when(update.message()).thenReturn(message);
        return update;
    }
}
