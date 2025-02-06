package backend.academy.bot.processor;

import backend.academy.bot.command.Command;
import backend.academy.bot.executor.RequestExecutor;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Component
public class UserMessageProcessor {

    private final RequestExecutor requestExecutor;
    private final List<Command> commandList;

    public SendMessage process(Update update) {
        for (Command command : commandList) {
            if (command.isCheck(update)) {
                return command.handle(update);
            }
        }
        return new SendMessage(update.message().chat().id(), "Команда не найдена");
    }
}
