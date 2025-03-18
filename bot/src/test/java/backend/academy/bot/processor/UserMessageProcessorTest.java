package backend.academy.bot.processor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import backend.academy.bot.command.Command;
import backend.academy.bot.command.base.TrackCommand;
import backend.academy.bot.state.UserState;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userMessageProcessor = new UserMessageProcessor(telegramBot, List.of(command1, trackCommand), userStateManager);
    }

    @Test
    @DisplayName("Обработка сообщения: команда найдена и обработана")
    void testProcess_CommandFoundAndHandled() {
        Update update = createUpdateWithText("/mock");
        when(command1.matchesCommand(update)).thenReturn(true);
        when(command1.handle(update)).thenReturn(new SendMessage(123L, "Mock message"));

        SendMessage result = userMessageProcessor.process(update);
        verify(command1, times(1)).matchesCommand(update);
        verify(command1, times(1)).handle(update);
        assertEquals("Mock message", result.getParameters().get("text"));
    }

    @Test
    @DisplayName("Обработка сообщения: команда не найдена, состояние WAITING_URL")
    void testProcess_NoCommandFound_WaitingUrlState() {
        Update update = createUpdateWithText("https://github.com/example");
        when(command1.matchesCommand(update)).thenReturn(false);
        when(userStateManager.getUserState(123L)).thenReturn(UserState.WAITING_URL);
        when(trackCommand.handle(update)).thenReturn(new SendMessage(123L, "Track command handled"));

        SendMessage result = userMessageProcessor.process(update);

        verify(command1, times(1)).matchesCommand(update);
        verify(trackCommand, times(1)).handle(update);
        assertEquals("Track command handled", result.getParameters().get("text"));
    }

    @Test
    @DisplayName("Обработка сообщения: пользователь создается, если не существует")
    void testProcess_UserCreatedIfNotExist() {
        Update update = createUpdateWithText("/start");
        when(command1.matchesCommand(update)).thenReturn(true);
        when(command1.handle(update)).thenReturn(new SendMessage(123L, "User created"));

        userMessageProcessor.process(update);

        verify(userStateManager, times(1)).createUserIfNotExist(123L);
    }

    private Update createUpdateWithText(String text) {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);
        when(message.text()).thenReturn(text);

        return update;
    }
}
