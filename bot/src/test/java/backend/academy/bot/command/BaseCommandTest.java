package backend.academy.bot.command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class BaseCommandTest {
    Update getMockUpdate(Long id, String text) {
        Update update = mock(Update.class);
        Chat chat = mock(Chat.class);
        when(chat.id()).thenReturn(id);
        Message message = mock(Message.class);
        when(message.text()).thenReturn(text);
        when(message.chat()).thenReturn(chat);
        when(update.message()).thenReturn(message);

        return update;
    }
}
