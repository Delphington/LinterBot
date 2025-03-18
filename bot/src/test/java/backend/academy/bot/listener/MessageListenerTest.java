package backend.academy.bot.listener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import backend.academy.bot.executor.RequestExecutor;
import backend.academy.bot.processor.UserMessageProcessor;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MessageListenerTest {

    @Mock
    private RequestExecutor requestExecutor;

    @Mock
    private UserMessageProcessor userMessageProcessor;

    private MessageListener messageListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageListener = new MessageListener(requestExecutor, userMessageProcessor);
    }

    @Test
    @DisplayName("Обработка валидного сообщения: сообщение отправляется через RequestExecutor")
    void testProcess_ValidMessage_SendsResponse() {
        // Arrange
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("Test message");

        SendMessage sendMessage = new SendMessage("1", "Test message");
        when(userMessageProcessor.process(update)).thenReturn(sendMessage);

        // Act
        int result = messageListener.process(List.of(update));

        // Assert
        verify(userMessageProcessor, times(1)).process(update);
        verify(requestExecutor, times(1)).execute(sendMessage);
        assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, result);
    }

    @Test
    @DisplayName("Обработка Update с null-сообщением: обработка не происходит")
    void testProcess_MessageIsNull_DoesNotProcess() {
        // Arrange
        Update update = mock(Update.class);
        when(update.message()).thenReturn(null);

        // Act
        int result = messageListener.process(List.of(update));

        // Assert
        verify(userMessageProcessor, never()).process(any());
        verify(requestExecutor, never()).execute(any());
        assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, result);
    }

    @Test
    @DisplayName("Обработка сообщения: UserMessageProcessor возвращает null, запрос не отправляется")
    void testProcess_UserMessageProcessorReturnsNull_DoesNotExecute() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("Test message");

        when(userMessageProcessor.process(update)).thenReturn(null);

        int result = messageListener.process(List.of(update));

        verify(userMessageProcessor, times(1)).process(update);
        verify(requestExecutor, never()).execute(any());
        assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, result);
    }
}
