package backend.academy.bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class MessageListener implements UpdatesListener {
    @Override
    public int process(List<Update> list) {
        return 0;
    }
}
