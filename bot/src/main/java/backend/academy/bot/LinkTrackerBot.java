package backend.academy.bot;


import backend.academy.bot.listener.MessageListener;
import com.pengrad.telegrambot.TelegramBot;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Getter
@Component
public class LinkTrackerBot implements Bot {

    private final TelegramBot telegramBot;
    private final MessageListener messageListener;

    @Override
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(messageListener);
    }


    @Override
    public void close() {
        telegramBot.shutdown();
    }
}
