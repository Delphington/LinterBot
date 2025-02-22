package backend.academy.bot.command;

import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.api.ScrapperClient;
import backend.academy.bot.state.UserState;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class StartCommand implements Command {

    private final ScrapperClient scrapperClient;
    private final UserStateManager userStateManager;

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return "Начинает работу бота";
    }

    @Override
    public SendMessage handle(Update update) {
        userStateManager.setUserStatus(update.message().chat().id(), UserState.WAITING_COMMAND);

        String message = "Привет! Используй /help чтобы увидеть все команды";
        try {
            scrapperClient.registerChat(update.message().chat().id());
        } catch (ResponseException e) {
            message = "Ты уже зарегистрировался :)";
            log.warn("Не корректные поведение с регистрацией {}", update.message().chat().id());
        }
        return new SendMessage(update.message().chat().id(), message);
    }
}
