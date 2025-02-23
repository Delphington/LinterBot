package backend.academy.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public interface Command {

    String command();

    String description();

    SendMessage handle(Update update);

    default boolean isCheck(Update update) {
        return update.message().text() != null
               && update.message().text().split(" +")[0].equals(command());
    }
}
