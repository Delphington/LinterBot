package backend.academy.bot.command.link;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import backend.academy.bot.api.dto.request.AddLinkRequest;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.command.TestUtils;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.kafka.client.KafkaInvalidLinkProducer;
import backend.academy.bot.message.ParserMessage;
import backend.academy.bot.redis.RedisCacheService;
import backend.academy.bot.state.UserState;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TrackCommandTest implements TestUtils {

    private TrackCommand trackCommand;

    @Mock
    private ScrapperClient scrapperClient;

    @Mock
    private UserStateManager userStateManager;

    @Mock
    private ParserMessage parserMessage;

    @Mock
    private RedisCacheService redisCacheService;

    @Mock
    private KafkaInvalidLinkProducer kafkaInvalidLinkProducer;

    private static final Long USER_ID = 6758392L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trackCommand = new TrackCommand(
                scrapperClient, parserMessage, userStateManager, redisCacheService, kafkaInvalidLinkProducer);
    }

    @DisplayName("Проверка наименования команды")
    @Test
    void testCommandTrack() {
        Assertions.assertEquals("/track", trackCommand.command());
    }

    @DisplayName("Проверка описания")
    @Test
    void testCommandDescription() {
        Assertions.assertEquals("Добавляет ссылку для отслеживания", trackCommand.description());
    }

    @Test
    @DisplayName("Ввод верной ссылки")
    void handleCorrectUrlShouldReturnSuccessResponse() {
        // Arrange
        String commandMessage = "/track https://github.com/";
        Update update = getMockUpdate(USER_ID, commandMessage);

        when(userStateManager.getUserState(USER_ID)).thenReturn(UserState.WAITING_URL);

        // Act
        SendMessage sendMessage = trackCommand.handle(update);

        // Assert
        Assertions.assertEquals(
                "Введите теги через пробел для ссылки",
                sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Ввод неправильной ссылки")
    void handleIncorrectUrl() {
        // Arrange
        String commandMessage = "/track http://giф";
        Update update = getMockUpdate(USER_ID, commandMessage);

        when(userStateManager.getUserState(USER_ID)).thenReturn(UserState.WAITING_URL);

        doThrow(new InvalidInputFormatException("Use a valid URL as a parameter in the form like '/track <link>'"))
                .when(parserMessage)
                .parseUrl(commandMessage, UserState.WAITING_URL);

        // Act
        SendMessage sendMessage = trackCommand.handle(update);

        // Assert
        Assertions.assertEquals(
                "Use a valid URL as a parameter in the form like '/track <link>'",
                sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Проверка введение фильтров")
    void handleTagsInput() {
        // Arrange
        String tagsMessage = "tag1 tag2";
        Update update = getMockUpdate(USER_ID, tagsMessage);

        when(userStateManager.getUserState(USER_ID)).thenReturn(UserState.WAITING_TAGS);

        SendMessage sendMessage = trackCommand.handle(update);

        Assertions.assertEquals(
                "Введите фильтры через пробел для ссылки",
                sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Повторное добавление ссылки")
    void handleDuplicateLink() {
        String filtersMessage = "filter1 filter2";
        Update update = getMockUpdate(USER_ID, filtersMessage);

        when(userStateManager.getUserState(USER_ID)).thenReturn(UserState.WAITING_FILTERS);

        when(scrapperClient.trackLink(eq(USER_ID), any(AddLinkRequest.class)))
                .thenThrow(new ResponseException("Link already exists"));

        SendMessage sendMessage = trackCommand.handle(update);

        Assertions.assertEquals(
                "Такая ссылка уже добавлена, добавьте новую ссылку используя /track",
                sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Проверка пустых тегов")
    void handleInvalidTagsInput() {
        String invalidTagsMessage = "";
        Update update = getMockUpdate(USER_ID, invalidTagsMessage);

        when(userStateManager.getUserState(USER_ID)).thenReturn(UserState.WAITING_TAGS);

        doThrow(new InvalidInputFormatException("Теги не могут быть пустыми"))
                .when(parserMessage)
                .getAdditionalAttribute(invalidTagsMessage);

        SendMessage sendMessage = trackCommand.handle(update);

        Assertions.assertEquals(
                "Теги не могут быть пустыми", sendMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Проверка пустых фильтров")
    void handleInvalidFiltersInput() {
        String invalidFiltersMessage = "";
        Update update = getMockUpdate(USER_ID, invalidFiltersMessage);

        when(userStateManager.getUserState(USER_ID)).thenReturn(UserState.WAITING_FILTERS);

        doThrow(new InvalidInputFormatException("Фильтры не могут быть пустыми"))
                .when(parserMessage)
                .getAdditionalAttribute(invalidFiltersMessage);

        SendMessage sendMessage = trackCommand.handle(update);

        Assertions.assertEquals(
                "Фильтры не могут быть пустыми", sendMessage.getParameters().get("text"));
    }
}
