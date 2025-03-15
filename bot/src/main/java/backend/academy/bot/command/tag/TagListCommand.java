package backend.academy.bot.command.tag;

import backend.academy.bot.api.dto.response.TagListResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.command.Command;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TagListCommand implements Command {

    private final ScrapperClient scrapperClient;
    private final ParserMessage parserMessage;

    @Override
    public String command() {
        return "/taglist";
    }

    @Override
    public String description() {
        return "Выводит все теги пользователя";
    }

    @Override
    public SendMessage handle(Update update) {
        Long id = update.message().chat().id();
        try {
            parserMessage.parseMessageTagList(update.message().text().trim());
        } catch (InvalidInputFormatException e) {
            return new SendMessage(id, e.getMessage());
        }
        try {
            TagListResponse tagListResponse = scrapperClient.getAllListLinksByTag(id);
            return new SendMessage(id, createMessage(tagListResponse));
        } catch (ResponseException e) {
            log.error("Ошибка при /taglist {}", e.getMessage());
            return new SendMessage(id, "Ошибка попробуй еще раз");
        }
    }


    private String createMessage(TagListResponse tagListResponse) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ваши теги:\n");
        for (int i = 0; i < tagListResponse.tags().size(); i++) {
            sb.append((i + 1) + ") ").append(tagListResponse.tags().get(i)).append("\n");
        }
        return sb.toString();
    }
}
