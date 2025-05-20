package backend.academy.bot.metrics;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import backend.academy.bot.command.Command;
import backend.academy.bot.command.link.TrackCommand;
import backend.academy.bot.processor.UserMessageProcessor;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserMessageProcessorTest {

    @Mock
    private TelegramBot telegramBot;

    @Mock
    private Command command1;

    @Mock
    private TrackCommand trackCommand;

    @Mock
    private UserStateManager userStateManager;

    private UserMessageProcessor userMessageProcessor;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        meterRegistry = new SimpleMeterRegistry(); // Используем реальный MeterRegistry
        userMessageProcessor =
                new UserMessageProcessor(telegramBot, List.of(command1, trackCommand), userStateManager, meterRegistry);
    }

    @Test
    @DisplayName("Обработка сообщения: пользователь создается, если не существует")
    void testProcess_UserCreatedIfNotExist() {
        Update update = createUpdateWithText("/start");
        when(command1.matchesCommand(update)).thenReturn(true);
        when(command1.handle(update)).thenReturn(new SendMessage("123", "User created"));

        userMessageProcessor.process(update);

        // Проверяем метрику
        Counter counter = meterRegistry.counter("msg_count");
        assertEquals(1, counter.count());

        verify(userStateManager, times(1)).createUserIfNotExist(123L);
    }

    private Update createUpdateWithText(String text) {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);
        return update;
    }
}
