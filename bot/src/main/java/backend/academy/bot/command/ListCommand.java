package backend.academy.bot.command;

import backend.academy.bot.service.LinkTrackerService;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ListCommand implements Command {

    private final LinkTrackerService linkTrackerService;

    @Override
    public String command() {
        return "/list";
    }

    @Override
    public String description() {
        return "Выводит список отслеживаемых ссылок";
    }

    @Override
    public SendMessage handle(Update update) {
        Long id = update.message().chat().id();
        Optional<List<String>> op = linkTrackerService.findAll(id);

        if (op.isEmpty()) {
            return new SendMessage(update.message().chat().id(), "Никакие ссылки еще не отслеживаются");
        }

        return new SendMessage(update.message().chat().id(), createMessage(op.get()));
    }


    private String createMessage(List<String> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("Отслеживаемые ссылки: \n");
        for (int i = 0; i < list.size(); i++) {
            sb.append(i + 1).append(") ").append(list.get(i)).append("\n");
        }

        return sb.toString();
    }
}
