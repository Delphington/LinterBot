package backend.academy.bot.command;

import backend.academy.bot.service.BotService;
import backend.academy.bot.service.UserService;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StartCommand implements Command {


    private final UserService userService;


    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return "Начинает работу бота";
    }

    public void saveUser(Update update){
        userService.save(update.message().chat().id(),update.message().chat().firstName());
    }

    @Override
    public SendMessage handle(Update update) {
        saveUser(update);
        return new SendMessage(update.message().chat().id(), "Hello my friend");
    }
}
