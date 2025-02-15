package backend.academy.bot.listener;

import backend.academy.bot.executor.RequestExecutor;
import backend.academy.bot.processor.UserMessageProcessor;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Component
public class MessageListener implements UpdatesListener {

    //Для запроса к Telegram API
    private final RequestExecutor requestExecutor;

    //Обработка сообщений пользователь и какую команду вызвать
    private final UserMessageProcessor userMessageProcessor;

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            if (update.message() != null) {

                log.info("MessageLister: {}", update.message().text());
                SendMessage sendMessage = userMessageProcessor.process(update);
                if (sendMessage != null) {
                    requestExecutor.execute(sendMessage);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
