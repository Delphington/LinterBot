package backend.academy.bot;


import backend.academy.bot.command.AbstractCommand;
import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class Bot implements ExceptionHandler, AutoCloseable {

    private final TelegramBot telegramBot;
    // Слушатель для обработки входящих сообщений
    private final MessageListener messageListener;
    //Обработчик сообщений пользователей. Он отвечает за обработку команд,
    // извлечение информации из сообщений и т.д.
    private MessagesProcessor messagesProcessor;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        log.info("Инициализация команд бота..."); // Запись в лог информации об инициализации команд


        List<AbstractCommand> list = messagesProcessor.commands();

        List<BotCommand> apiCommands = new ArrayList<>();
        for (AbstractCommand item : list) {
            apiCommands.add(item.toApiCommand());
        }

        // Устанавливаем команды бота через Executor
        execute(new SetMyCommands(apiCommands.toArray(new BotCommand[0])));

        // Настраиваем обработчик сообщений
        telegramBot.setUpdatesListener(messageListener);

        log.info("Бот успешно инициализирован.");
    }


    // Универсальный метод для выполнения запросов к Telegram API
    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(BaseRequest<T, R> request) {
        telegramBot.execute(request); // Выполняет запрос с помощью объекта TelegramBot
    }


    @Override
    public void close() throws Exception {
        telegramBot.shutdown();
    }

    @Override
    public void onException(TelegramException e) {
        log.error("Error: " + e.getMessage());
    }
}
