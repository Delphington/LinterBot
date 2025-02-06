package backend.academy.bot.processor;

import backend.academy.bot.command.Command;
import backend.academy.bot.command.Commands;
import backend.academy.bot.executor.RequestExecutor;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Getter
@Component
public class UserMessageProcessor {

    private final RequestExecutor requestExecutor;
    private final Commands commands;

    public SendMessage process(Update update) {
        for (Command command : commands.commandList()) {
            if (command.isCheck(update)) {
                return command.handle(update);
            }
        }
        return new SendMessage(update.message().chat().id(), "Команда не найдена");
    }
}
