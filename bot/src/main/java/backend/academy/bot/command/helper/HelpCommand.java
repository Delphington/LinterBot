package backend.academy.bot.command.helper;

import backend.academy.bot.command.Command;
import backend.academy.bot.state.UserState;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
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
        log.info("Команда /help выполнена {}", update.message().chat().id());
        return new SendMessage(update.message().chat().id(), getListCommandMessage());
    }

    private String getListCommandMessage() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).command()).append(" -- ").append(list.get(i).description());
            if (i != list.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
