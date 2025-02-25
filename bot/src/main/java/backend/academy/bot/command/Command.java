package backend.academy.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public interface Command {

    String command();

    String description();

    SendMessage handle(Update update);

    default boolean isCheck(Update update) {
        if (update.message().text() == null) {
            return false;
        }

        String[] parts = update.message().text().split(" +", 2);
        return parts.length > 0 && parts[0].equals(command());
    }
}
