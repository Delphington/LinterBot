package backend.academy.bot.command;

import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.api.ScrapperClient;
import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import backend.academy.bot.state.UserState;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import java.net.URI;

@Log4j2
@RequiredArgsConstructor
@Component
public class UntrackCommand implements Command {

    private final ScrapperClient scrapperClient;
    private final ParserMessage parserMessage;
    private final UserStateManager userStateManager;

    @Override
    public String command() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "Удаляет ссылку для отслеживания";
    }

    @Override
    public SendMessage handle(Update update) {
        userStateManager.setUserStatus(update.message().chat().id(), UserState.WAITING_COMMAND);

        Long id = update.message().chat().id();

        URI uri;

        try {
            uri = parserMessage.parseUrl(update.message().text());
        } catch (InvalidInputFormatException e) {
            return new SendMessage(id, e.getMessage());
        }

        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(uri);
        LinkResponse linkResponse;

        try {
            linkResponse = scrapperClient.untrackLink(id, removeLinkRequest);
        } catch (ResponseException e) {
            log.error("Ошибочка {}", e.getMessage());
            return new SendMessage(id, "Ссылка не найдена");
        }
        String stringLog = String.format("Ссылка удаленна %s", linkResponse.url());
        log.info("Ссылка удалена! {}", stringLog);
        return new SendMessage(id, stringLog);

    }
}
