package backend.academy.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements Command {
    @Override
    public String command() {
        return "/help";
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public SendMessage handle(Update update) {



        return new SendMessage(update.message().chat().id(), "I can help you");
    }
}
