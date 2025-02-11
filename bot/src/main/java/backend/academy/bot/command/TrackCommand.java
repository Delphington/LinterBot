package backend.academy.bot.command;

import backend.academy.bot.api.ResponseException;
import backend.academy.bot.api.ScrapperClient;
import backend.academy.bot.api.dto.request.AddLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@RequiredArgsConstructor
@Component
public class TrackCommand implements Command {

    private final ScrapperClient scrapperClient;

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
        String url;

        try {
            url = update.message().text().split(" ")[1];
        } catch (RuntimeException e) {
            return new SendMessage(update.message().chat().id(), "Попробуй ввести ссылку вместе с командой /track");
        }

        //-------------------------
        URI uri;

        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            return new SendMessage(update.message().chat().id(), "Ошибка преобразования в url Попробуй ввести ссылку вместе с командой /track");
        }

        AddLinkRequest addLinkRequest = new AddLinkRequest(uri, null, null);

        LinkResponse linkResponse;
        try {
            linkResponse = scrapperClient.trackLink(update.message().chat().id(), addLinkRequest);
        } catch (ResponseException e) {
            log.error("Ошибка (скорее всего дубликат ссылки) " + e.getMessage());
            return new SendMessage(update.message().chat().id(), "Такая ссылка уже добавлена");
        } catch (RuntimeException e) {
            return new SendMessage(update.message().chat().id(), "МЫ НЕ ДОЛЖНЫ БЫТЬ ТУТ");

        }
        String stringLog = String.format("Ссылка добавлена! Отслеживание id: %d url: %s", linkResponse.id(), linkResponse.url());
        log.info("Ссылка добавлена!" + stringLog);
        return new SendMessage(update.message().chat().id(), stringLog);

    }

}
