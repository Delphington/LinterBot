package backend.academy.bot.processor;

import backend.academy.bot.command.Command;
import backend.academy.bot.command.base.TrackCommand;
import backend.academy.bot.state.UserState;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Getter
@Component
public class UserMessageProcessor {

    private final TelegramBot telegramBot;
    private final List<Command> commandList;
    private final UserStateManager userStateManager;

    public void registerCommands() {
        List<BotCommand> commands = commandList.stream()
                .map(command -> new BotCommand(command.command(), command.description()))
                .toList();

        SetMyCommands setMyCommands = new SetMyCommands(commands.toArray(new BotCommand[0]));
        BaseResponse response = telegramBot.execute(setMyCommands);

        if (response.isOk()) {
            log.info("Команды успешно зарегистрированы в Telegram.");
        } else {
            log.error("Ошибка при регистрации команд: {}", response.description());
        }
    }

    public SendMessage process(Update update) {
        Long id = update.message().chat().id();
        userStateManager.createUserIfNotExist(id);

        for (Command command : commandList) {
            if (command.matchesCommand(update)) {
                return command.handle(update);
            }
        }

        // Если мы вводим url
        switch (userStateManager.getUserState(id)) {
            case WAITING_URL, WAITING_TAGS, WAITING_FILTERS -> {
                try {
                    return getTrackCommand().handle(update);
                } catch (IllegalStateException e) {
                    log.warn("Команда не найдена {}", e.getMessage());
                }
            }
            default -> {
                userStateManager.setUserStatus(id, UserState.WAITING_URL);
                throw new IllegalStateException("Unexpected value: " + userStateManager.getUserState(id));
            }
        }

        return new SendMessage(update.message().chat().id(), "Команда не найдена");
    }

    private Command getTrackCommand() {
        return commandList.stream()
                .filter(TrackCommand.class::isInstance)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("TrackCommand not found"));
    }
}
