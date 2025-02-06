package backend.academy.bot.processor;

import backend.academy.bot.command.Command;
import backend.academy.bot.command.HelpCommand;
import backend.academy.bot.command.ListCommand;
import backend.academy.bot.command.StartCommand;
import backend.academy.bot.command.TrackCommand;
import backend.academy.bot.command.UntrackCommand;
import backend.academy.bot.executor.RequestExecutor;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;

@Getter
@Component
public class UserMessageProcessor {

    @Autowired
    private RequestExecutor requestExecutor;
    private ArrayList<Command> commands;

    public UserMessageProcessor() {
        commands = new ArrayList<>();
        commands.add(new HelpCommand());
        commands.add(new StartCommand());
        commands.add(new ListCommand());
        commands.add(new TrackCommand());
        commands.add(new UntrackCommand());

    }


    public SendMessage process(Update update) {
        for (Command command : commands) {
            if (command.supports(update)) {
                return command.handle(update);
            }
        }
        return new SendMessage(update.message().chat().id(), "Команда не найдена");
    }

}
