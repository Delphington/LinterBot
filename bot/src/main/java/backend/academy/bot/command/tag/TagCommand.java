package backend.academy.bot.command.tag;

import backend.academy.bot.api.dto.request.tag.TagLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.command.Command;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TagCommand implements Command {

    private final ScrapperClient scrapperClient;
    private final ParserMessage parserMessage;

    @Override
    public String command() {
        return "/tag"; // /tag name_tags -> list<Link>
    }

    @Override
    public String description() {
        return "Позволяет выводить ссылки по тегам";
    }

    @Override
    public SendMessage handle(Update update) {
        String tag;

        try {
            tag = parserMessage.parseMessageTag(update.message().text().trim());
        } catch (InvalidInputFormatException e) {
            log.info(
                    "Не корректные поведение с /tag {}", update.message().chat().id());
            return new SendMessage(update.message().chat().id(), e.getMessage());
        }

        StringBuilder message = new StringBuilder("С тегом: " + tag + "\n");
        try {
            ListLinksResponse listLink =
                    scrapperClient.getListLinksByTag(update.message().chat().id(), new TagLinkRequest(tag));
            if (listLink.links().isEmpty()) {
                message.append("Никакие ссылки не отслеживаются");
            } else {
                message.append(createMessage(listLink.links()));
            }

        } catch (ResponseException e) {
            log.info(
                    "Не корректные получение тегов из БД {}",
                    update.message().chat().id());
            message.append("Ошибка! попробуй еще раз");
        }

        return new SendMessage(update.message().chat().id(), message.toString());
    }

    private String createMessage(List<LinkResponse> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("Отслеживаемые ссылки:\n");
        for (int i = 0; i < list.size(); i++) {
            sb.append(i + 1).append(") ");
            sb.append("URL:").append(list.get(i).url()).append("\n");
        }
        return sb.toString();
    }
}
