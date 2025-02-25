package backend.academy.bot.command;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import backend.academy.bot.api.ScrapperClient;
import backend.academy.bot.api.dto.request.AddLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import backend.academy.bot.state.UserState;
import backend.academy.bot.state.UserStateManager;
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
import java.net.URI;
import java.util.List;

public class TrackCommandTest extends BaseCommandTest {

    @Autowired
    private TrackCommand trackCommand;

    @Autowired
    private ScrapperClient scrapperClient;

    @Autowired
    private UserStateManager userStateManager;

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
        public UserStateManager userStateManager() {
            return Mockito.mock(UserStateManager.class);
        }

        @Bean
        public ParserMessage parserMessage() {
            return Mockito.mock(ParserMessage.class);
        }
    }

    @Test
    @DisplayName("Ввод верной ссылки")
    void handleCorrectUrlShouldReturnSuccessResponse() {
        // Arrange
        String commandMessage = "/track https://github.com/";
        Update update = getMockUpdate(5L, commandMessage);

        when(userStateManager.getUserState(5L)).thenReturn(UserState.WAITING_URL);

        // Act
        SendMessage sendMessage = trackCommand.handle(update);

        // Assert
        assertEquals(
            "Введите теги через пробел для ссылки",
            sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Ввод неправильной ссылки")
    void handleIncorrectUrl() {
        // Arrange
        String commandMessage = "/track http://giф";
        Update update = getMockUpdate(5L, commandMessage);

        when(userStateManager.getUserState(5L)).thenReturn(UserState.WAITING_URL);

        doThrow(new InvalidInputFormatException("Use a valid URL as a parameter in the form like '/track <link>'"))
            .when(parserMessage)
            .parseUrl(commandMessage, UserState.WAITING_URL);

        // Act
        SendMessage sendMessage = trackCommand.handle(update);

        // Assert
        assertEquals(
            "Use a valid URL as a parameter in the form like '/track <link>'",
            sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Проверка введение фильтров")
    void handleTagsInput() {
        // Arrange
        Long chatId = 5L;
        String tagsMessage = "tag1 tag2";
        Update update = getMockUpdate(chatId, tagsMessage);

        when(userStateManager.getUserState(chatId)).thenReturn(UserState.WAITING_TAGS);

        SendMessage sendMessage = trackCommand.handle(update);

        assertEquals(
            "Введите фильтры через пробел для ссылки",
            sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Повторное добавление ссылки")
    void handleDuplicateLink() {
        Long chatId = 5L;
        String filtersMessage = "filter1 filter2";
        Update update = getMockUpdate(chatId, filtersMessage);

        when(userStateManager.getUserState(chatId)).thenReturn(UserState.WAITING_FILTERS);

        when(scrapperClient.trackLink(eq(chatId), any(AddLinkRequest.class)))
            .thenThrow(new ResponseException("Link already exists"));

        SendMessage sendMessage = trackCommand.handle(update);

        assertEquals(
            "Такая ссылка уже добавлена, добавьте новую ссылку используя /track",
            sendMessage.getParameters().get("text"));
    }

//    @Test
//    @DisplayName("Успешное добавление ссылки тегов и фильтров")
//    void handleFiltersInput() {
//        Long chatId = 5L;
//        String filtersMessage = "filter1 filter2";
//        Update update = getMockUpdate(chatId, filtersMessage);
//
//        when(userStateManager.getUserState(chatId)).thenReturn(UserState.WAITING_FILTERS);
//
//        LinkResponse linkResponse = new LinkResponse(
//            1L,
//            URI.create("https://github.com/"),
//            List.of("tag1", "tag2"),
//            List.of("filter1", "filter2")
//        );
//        when(scrapperClient.trackLink(eq(chatId), any(AddLinkRequest.class))).thenReturn(linkResponse);
//
//        SendMessage sendMessage = trackCommand.handle(update);
//
//        String expectedMessage = "Ссылка добавлена!\n" +
//            "URL: https://github.com/\n" +
//            "tags: [tag1, tag2]\n" +
//            "filters: [filter1, filter2]";
//        assertEquals(expectedMessage, sendMessage.getParameters().get("text"));
//    }

    @Test
    @DisplayName("Проверка пустых тегов")
    void handleInvalidTagsInput() {
        Long chatId = 5L;
        String invalidTagsMessage = "";
        Update update = getMockUpdate(chatId, invalidTagsMessage);

        when(userStateManager.getUserState(chatId)).thenReturn(UserState.WAITING_TAGS);

        doThrow(new InvalidInputFormatException("Теги не могут быть пустыми"))
            .when(parserMessage)
            .getAdditionalAttribute(invalidTagsMessage);

        SendMessage sendMessage = trackCommand.handle(update);

        assertEquals("Теги не могут быть пустыми", sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Проверка пустых фильтров")
    void handleInvalidFiltersInput() {
        Long chatId = 5L;
        String invalidFiltersMessage = "";
        Update update = getMockUpdate(chatId, invalidFiltersMessage);

        when(userStateManager.getUserState(chatId)).thenReturn(UserState.WAITING_FILTERS);

        doThrow(new InvalidInputFormatException("Фильтры не могут быть пустыми"))
            .when(parserMessage)
            .getAdditionalAttribute(invalidFiltersMessage);

        SendMessage sendMessage = trackCommand.handle(update);

        assertEquals(
            "Фильтры не могут быть пустыми", sendMessage.getParameters().get("text"));
    }
}
