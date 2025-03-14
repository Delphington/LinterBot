package backend.academy.bot.command.tag;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.command.Command;
import backend.academy.bot.message.ParserMessage;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TagListCommand implements Command {

    private final ScrapperClient scrapperClient;
    private final ParserMessage parserMessage;

    @Override
    public String command() {
        return "/tagList";
    }

    @Override
    public String description() {
        return "Выводит все теги пользователя";
    }

    @Override
    public SendMessage handle(Update update) {

        Long id = update.message().chat().id();



        return null;
    }
}
