package backend.academy.bot.command;

import backend.academy.bot.state.UserState;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@RequiredArgsConstructor
@Component
public class HelpCommand implements Command {

    private final List<Command> list;
    private final UserStateManager userStateManager;

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
        userStateManager.setUserStatus(update.message().chat().id(), UserState.WAITING_COMMAND);
        return new SendMessage(update.message().chat().id(), getListCommandMessage());
    }

    private String getListCommandMessage() {
        StringBuilder sb = new StringBuilder();
        for (Command command : list) {
            sb.append(command.command()).append(" -- ").
                append(command.description()).append("\n");
        }
        return sb.toString();
    }
}
