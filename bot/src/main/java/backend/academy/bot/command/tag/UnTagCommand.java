package backend.academy.bot.command.tag;

import backend.academy.bot.command.Command;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public class UnTagCommand implements Command {
    @Override
    public String command() {
        return "/untag"; // /untag link name_tag
    }

    @Override
    public String description() {
        return "Удаление тега у ссылок";
    }

    @Override
    public SendMessage handle(Update update) {
        return null;
    }
}
