package backend.academy.bot.command;

import backend.academy.bot.service.LinkTrackerService;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UntrackCommand implements Command {

    private final LinkTrackerService linkTrackerService;

    @Override
    public String command() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "Удаляет ссылку для отслеживания";
    }

    @Override
    public SendMessage handle(Update update) {
        Long id = update.message().chat().id();
        String url = update.message().text().split(" ")[1];
        String ans = linkTrackerService.deleteLink(id, url);
        return new SendMessage(update.message().chat().id(), ans);
    }
}
