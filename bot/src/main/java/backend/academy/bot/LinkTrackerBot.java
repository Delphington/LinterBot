package backend.academy.bot;

import backend.academy.bot.listener.MessageListener;
import backend.academy.bot.processor.UserMessageProcessor;
import com.pengrad.telegrambot.TelegramBot;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Getter
@Component
public class LinkTrackerBot implements AutoCloseable {

    private final TelegramBot telegramBot;
    private final MessageListener messageListener;
    private final UserMessageProcessor userMessageProcessor;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(messageListener);
        // Регистрируем команды при запуске
        userMessageProcessor.registerCommands();
    }

    @Override
    public void close() {
        telegramBot.shutdown();
    }
}
