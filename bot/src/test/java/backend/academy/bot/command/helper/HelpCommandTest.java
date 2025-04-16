package backend.academy.bot.command.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.command.filter.FilterCommand;
import backend.academy.bot.command.filter.FilterListCommand;
import backend.academy.bot.command.filter.UnFilterCommand;
import backend.academy.bot.command.link.ListCommand;
import backend.academy.bot.command.link.TrackCommand;
import backend.academy.bot.command.link.UntrackCommand;
import backend.academy.bot.command.tag.TagCommand;
import backend.academy.bot.command.tag.TagListCommand;
import backend.academy.bot.command.tag.UnTagCommand;
import backend.academy.bot.kafka.client.KafkaInvalidLinkProducer;
import backend.academy.bot.message.ParserMessage;
import backend.academy.bot.redis.RedisCacheService;
import backend.academy.bot.state.UserState;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HelpCommandTest {

    @Mock
    private UserStateManager userStateManager;

    @Mock
    private ScrapperClient scrapperClient;

    @Mock
    private ParserMessage parserMessage;

    @Mock
    private RedisCacheService redisCacheService;

    @Mock
    private KafkaInvalidLinkProducer kafkaInvalidLinkProducer;

    private HelpCommand helpCommand;

    private static final Long USER_ID = 10231L;

    @BeforeEach
    void setUp() {
        StartCommand startCommand = new StartCommand(scrapperClient, userStateManager);

        TagCommand tagCommand = new TagCommand(scrapperClient, parserMessage);
        TagListCommand tagCommandList = new TagListCommand(scrapperClient, parserMessage);
        UnTagCommand unTagCommand = new UnTagCommand(scrapperClient, parserMessage, redisCacheService);

        ListCommand listCommand = new ListCommand(scrapperClient, userStateManager, redisCacheService);
        TrackCommand trackCommand = new TrackCommand(
                scrapperClient, parserMessage, userStateManager, redisCacheService, kafkaInvalidLinkProducer);
        UntrackCommand untrackCommand =
                new UntrackCommand(scrapperClient, parserMessage, userStateManager, redisCacheService);

        FilterCommand filterCommand = new FilterCommand(scrapperClient, parserMessage);
        FilterListCommand filterListCommand = new FilterListCommand(scrapperClient, parserMessage);
        UnFilterCommand unFilterCommand = new UnFilterCommand(scrapperClient, parserMessage);

        helpCommand = new HelpCommand(
                List.of(
                        startCommand,
                        tagCommand,
                        tagCommandList,
                        unTagCommand,
                        listCommand,
                        trackCommand,
                        untrackCommand,
                        filterCommand,
                        filterListCommand,
                        unFilterCommand),
                userStateManager);
    }

    @Test
    @DisplayName("Проверка команды")
    void shouldReturnCorrectCommand() {
        Assertions.assertEquals("/help", helpCommand.command());
    }

    @Test
    @DisplayName("Проверка описания")
    void shouldReturnCorrectDescription() {
        Assertions.assertEquals("Выводит список всех доступных команд", helpCommand.description());
    }

    @Test
    @DisplayName("Обработка команды /help")
    void handle_shouldReturnListOfCommands() {
        // Act
        Update update = getMockUpdate(USER_ID);
        SendMessage result = helpCommand.handle(update);

        // Assert
        String expectedMessage =
                """
            /start -- Начинает работу бота
            /tag -- Позволяет выводить ссылки по тегам
            /taglist -- Выводит все теги пользователя
            /untag -- Удаление тега у ссылок
            /list -- Выводит список отслеживаемых ссылок
            /track -- Добавляет ссылку для отслеживания
            /untrack -- Удаляет ссылку для отслеживания
            /filter -- Позволяет добавить фильтрацию на получение уведомлений
            /filterlist -- Выводи все фильтры
            /unfilter -- Удаление фильтров
            """
                        .trim();

        assertEquals(expectedMessage, result.getParameters().get("text"));
        assertEquals(USER_ID, result.getParameters().get("chat_id"));
        verify(userStateManager).setUserStatus(USER_ID, UserState.WAITING_COMMAND);
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
