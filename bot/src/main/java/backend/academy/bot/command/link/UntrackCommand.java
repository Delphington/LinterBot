package backend.academy.bot.command.link;

import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.exception.ServiceUnavailableCircuitException;
import backend.academy.bot.client.link.ScrapperLinkClient;
import backend.academy.bot.command.Command;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import backend.academy.bot.redis.RedisCacheService;
import backend.academy.bot.state.UserState;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class UntrackCommand implements Command {

    private final ScrapperLinkClient scrapperLinkClient;
    private final ParserMessage parserMessage;
    private final UserStateManager userStateManager;
    private final RedisCacheService redisCacheService;

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
        Long id = update.message().chat().id();
        redisCacheService.invalidateCache(id);

        userStateManager.setUserStatus(update.message().chat().id(), UserState.WAITING_COMMAND);

        URI uri;
        try {
            uri = parserMessage.parseUrl(update.message().text());
        } catch (InvalidInputFormatException e) {
            log.warn(
                    "Пользователь пытается ввести не верную ссылку для удаления: {}",
                    update.message().chat().id());
            return new SendMessage(id, e.getMessage());
        }

        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(uri);
        LinkResponse linkResponse;
        try {
            linkResponse = scrapperLinkClient.untrackLink(id, removeLinkRequest);
        } catch (ResponseException e) {
            log.warn(
                    "Пользователь пытается удалить ссылку, который нет: {}",
                    update.message().chat().id());
            return new SendMessage(id, "Ссылка не найдена");
        } catch (ServiceUnavailableCircuitException e) {
            log.error("❌Service unavailable: {}", e.getMessage());
            return new SendMessage(
                    id, "⚠️ Сервис временно недоступен(Circuit). Пожалуйста, попробуйте через несколько минут.");
        } catch (Exception e) {
            return new SendMessage(id, "❌ Неизвестная ошибка при добавлении фильтра");
        }
        String stringLog = String.format("Ссылка удаленна %s", linkResponse.url());
        log.info("Команда /track выполнена {}", update.message().chat().id());
        return new SendMessage(id, stringLog);
    }
}
