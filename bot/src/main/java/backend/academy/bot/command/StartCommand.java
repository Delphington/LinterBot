package backend.academy.bot.command;

import backend.academy.bot.api.ResponseException;
import backend.academy.bot.api.ScrapperClient;
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
        String message = "Привет друг, " + update.message().chat().firstName();
        try {
            scrapperClient.registerChat(update.message().chat().id());
        } catch (ResponseException e) {
            message = "Не корректные параметры вводы :)";
            log.warn("Не корректные поведение с регистрацией {}", update.message().chat().id());
        }
        return new SendMessage(update.message().chat().id(), message);
    }
}
