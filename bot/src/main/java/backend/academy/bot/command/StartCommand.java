package backend.academy.bot.command;

import backend.academy.bot.api.ScrapperClient;
import backend.academy.bot.service.UserService;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class StartCommand implements Command {


    private final UserService userService;

    private final ScrapperClient scrapperClient;

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return "Начинает работу бота";
    }

    public void saveUser(Update update) {
        userService.save(update.message().chat().id(), update.message().chat().firstName());
    }

    @Override
    public SendMessage handle(Update update) {
        log.error("================================================");
        log.error("===  StartCommand ");

        scrapperClient.registerChat(update.message().chat().id());

      //  saveUser(update);



        return new SendMessage(update.message().chat().id(), "Hello my friend");
    }
}
