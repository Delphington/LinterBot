package backend.academy.bot.command.tag;

import backend.academy.bot.api.dto.request.tag.TagRemoveRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.command.Command;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import backend.academy.bot.redis.RedisCacheService;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UnTagCommand implements Command {

    private final ScrapperClient scrapperClient;
    private final ParserMessage parserMessage;
    private final RedisCacheService redisCacheService;

    @Override
    public String command() {
        return "/untag"; // /untag name_tag <link>
    }

    @Override
    public String description() {
        return "Удаление тега у ссылок";
    }

    @Override
    public SendMessage handle(Update update) {
        Long id = update.message().chat().id();
        redisCacheService.invalidateCache(id);
        TagRemoveRequest tg;
        try {
            tg = parserMessage.parseMessageUnTag(update.message().text());
        } catch (InvalidInputFormatException e) {
            return new SendMessage(id, e.getMessage());
        }
        try {
            return new SendMessage(id, createMessage(scrapperClient.removeTag(id, tg)));
        } catch (ResponseException e) {
            log.error("Ошибка удаление тега: {}", e.getMessage());
            return new SendMessage(id, "Ошибка: " + e.getMessage());
        }
    }

    private String createMessage(LinkResponse linkResponse) {
        return new StringBuilder()
                .append("Теги обновлены:")
                .append("\n")
                .append("Ссылка: ")
                .append(linkResponse.url())
                .append("\n")
                .append("Теги: ")
                .append(linkResponse.tags())
                .append("\n")
                .append("Фильтры: ")
                .append(linkResponse.filters())
                .toString();
    }
}
