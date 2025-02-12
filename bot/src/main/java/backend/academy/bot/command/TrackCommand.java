package backend.academy.bot.command;

import backend.academy.bot.api.ResponseException;
import backend.academy.bot.api.ScrapperClient;
import backend.academy.bot.api.dto.request.AddLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@Component
public class TrackCommand implements Command {

    private final ScrapperClient scrapperClient;
    private final ParserMessage parserMessage;

    @Override
    public String command() {
        return "/track";
    }

    @Override
    public String description() {
        return "Добавляет ссылку для отслеживания";
    }

    @Override
    public SendMessage handle(Update update) {
        Long id = update.message().chat().id();
        URI uri;

        try {
            uri = parserMessage.parseUrl(update.message().text());
        } catch (InvalidInputFormatException e) {
            return new SendMessage(id, e.getMessage());
        }

        AddLinkRequest addLinkRequest = new AddLinkRequest(uri, null, null);

        LinkResponse linkResponse;
        try {
            linkResponse = scrapperClient.trackLink(id, addLinkRequest);
        } catch (ResponseException e) {
            log.error("Ошибка (скорее всего дубликат ссылки) " + e.getMessage());
            return new SendMessage(id, "Такая ссылка уже добавлена");
        } catch (RuntimeException e) {
            return new SendMessage(id, "МЫ НЕ ДОЛЖНЫ БЫТЬ ТУТ");
        }
        String stringLog = String.format("Ссылка добавлена! Отслеживание id: %d url: %s", linkResponse.id(), linkResponse.url());
        log.info("Ссылка добавлена!" + stringLog);
        return new SendMessage(id, stringLog);

    }

}
