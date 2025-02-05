package backend.academy.bot;

import backend.academy.bot.command.AbstractCommand;
import java.util.List;


public interface MessagesProcessor {

    List<AbstractCommand> commands();
}
