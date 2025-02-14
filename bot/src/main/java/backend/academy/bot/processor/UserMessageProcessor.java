package backend.academy.bot.processor;

import backend.academy.bot.command.Command;
import backend.academy.bot.command.TrackCommand;
import backend.academy.bot.command.UserStateManager;
import backend.academy.bot.executor.RequestExecutor;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Getter
@Component
public class UserMessageProcessor {

    private final RequestExecutor requestExecutor;
    private final List<Command> commandList;
    private final UserStateManager userStateManager;


    public SendMessage process(Update update) {
        Long id = update.message().chat().id();
        userStateManager.createUserIfNotExist(id);

        for (Command command : commandList) {
            if (command.isCheck(update)) {
                return command.handle(update);
            }
        }

        // Если состояния ожидания
        switch (userStateManager.getUserState(id)) {
            case WAITING_URL -> {
                log.warn("Мы в статусе WAITING_URL");
                return getTrackCommand().handle(update);
            }
            case WAITING_TAGS ->{
                log.warn("Мы в статусе WAITING_TAGS");
                return getTrackCommand().handle(update);
            }
            case WAITING_FILTERS ->{
                log.warn("Мы в статусе WAITING_FILTERS");
                return getTrackCommand().handle(update);
            }

        }

        return new SendMessage(update.message().chat().id(), "Команда не найдена");
    }

    private Command getTrackCommand() {
        return commandList.stream().filter(TrackCommand.class::isInstance).findFirst().get();
    }
}
