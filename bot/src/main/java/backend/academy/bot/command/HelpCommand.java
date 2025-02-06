package backend.academy.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@RequiredArgsConstructor
@Component
public class HelpCommand implements Command {

    private final List<Command> list;

    @Override
    public String command() {
        return "/help";
    }

    @Override
    public String description() {
        return "Выводит список всех доступных команд";
    }

    @Override
    public SendMessage handle(Update update) {

        return new SendMessage(update.message().chat().id(), message());
    }

    private String message() {
        StringBuilder sb = new StringBuilder();
        for (Command command : list) {
            sb.append(command.command()).append(" -- ").
                append(command.description()).append("\n");
        }
        return sb.toString();
    }
}
