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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TrackCommand implements Command {

    private final ScrapperClient scrapperClient;
    private final ParserMessage parserMessage;
    private final UserStateManager userStateManager;

    @Override
    public String command() {
        return "/track";
    }

    @Override
    public String description() {
        return "Добавляет ссылку для отслеживания";
    }

    //    @Override
//    public SendMessage handle(Update update) {
//        Long id = update.message().chat().id();
//        URI uri;
//
//        try {
//            uri = parserMessage.parseUrl(update.message().text());
//        } catch (InvalidInputFormatException e) {
//            userStateManager.setUserStatus(id, UserState.WAITING_URL);
//            log.error("МЫ ТАМ ГДЕ НУЖНО ждем url");
//            return new SendMessage(id, e.getMessage());
//        }
//
//        AddLinkRequest addLinkRequest = new AddLinkRequest(uri, null, null);
//
//        LinkResponse linkResponse;
//        try {
//            linkResponse = scrapperClient.trackLink(id, addLinkRequest);
//        } catch (ResponseException e) {
//            log.error("Ошибка (скорее всего дубликат ссылки) " + e.getMessage());
//            return new SendMessage(id, "Такая ссылка уже добавлена");
//        } catch (RuntimeException e) {
//            return new SendMessage(id, "МЫ НЕ ДОЛЖНЫ БЫТЬ ТУТ");
//        }
//        String stringLog = String.format("Ссылка добавлена! Отслеживание id: %d url: %s", linkResponse.id(), linkResponse.url());
//        log.info("Ссылка добавлена!" + stringLog);
//        return new SendMessage(id, stringLog);
//
//    }

    @SneakyThrows
    public SendMessage handle(Update update) {
        Long id = update.message().chat().id();
        URI uri;

        if (UserState.WAITING_COMMAND == userStateManager.getUserState(id) ||
            UserState.WAITING_URL == userStateManager.getUserState(id)) {

            try {
                log.error("Вывод статуса: " + userStateManager.getUserState(id));
                uri = parserMessage.isValidateTrackInput(update.message().text().trim(),
                    userStateManager.getUserState(id));
            } catch (InvalidInputFormatException e) {
                userStateManager.setUserStatus(id, UserState.WAITING_URL);
                log.error("Пользователь: неверно ввел /track, статус -> WAITING_URL");
                return new SendMessage(id, e.getMessage());
            }

            userStateManager.setUserStatus(id, UserState.WAITING_TAGS);
            userStateManager.addUserURI(id, uri);


            String stringLog = String.format("Введите тэги через пробел для ссылки");
            return new SendMessage(id, stringLog);

        } else if (userStateManager.getUserState(id) == UserState.WAITING_TAGS) {
            log.error("==========================================================");

            List<String> listTags;
            try {
                listTags = parserMessage.getAdditionalAttribute(update.message().text().trim());
            } catch (InvalidInputFormatException e) {
                log.error("Пользователь: не ввел теги");
                return new SendMessage(id, e.getMessage());
            }

            //Устанавливает теги
            userStateManager.addUserTags(id, listTags);
            userStateManager.setUserStatus(id, UserState.WAITING_FILTERS);

            String stringLog = String.format("Введите фильтры через пробел для ссылки");
            return new SendMessage(id, stringLog);
        } else if (userStateManager.getUserState(id) == UserState.WAITING_FILTERS) {
            List<String> listFilters;
            try {
                listFilters = parserMessage.getAdditionalAttribute(update.message().text().trim());
            } catch (InvalidInputFormatException e) {
                log.error("Пользователь: не ввел фильтр");
                return new SendMessage(id, e.getMessage());
            }

            //Устанавливает теги
            userStateManager.addUserFilters(id, listFilters);
            userStateManager.setUserStatus(id, UserState.PRE_END);

        }
        if (userStateManager.getUserState(id) == UserState.PRE_END) {

            AddLinkRequest addLinkRequest = new AddLinkRequest(userStateManager.getURIByUserId(id),
                userStateManager.getListTagsByUserId(id), userStateManager.getListFiltersByUserId(id));

            LinkResponse linkResponse;
            try {
                linkResponse = scrapperClient.trackLink(id, addLinkRequest);
            } catch (ResponseException e) {
                log.error("Ошибка (скорее всего дубликат ссылки) " + e.getMessage());
                return new SendMessage(id, "Такая ссылка уже добавлена");
            }
            String stringLog = String.format("Ссылка добавлена!\nURL: %s\ntags: %s\nfilters: %s",
                linkResponse.url(), linkResponse.tags(), linkResponse.filters());
            log.info("Ссылка добавлена!" + stringLog);
            userStateManager.clear(id);
            userStateManager.clearUseURIMap(id);
            return new SendMessage(id, stringLog);
        }

        return new SendMessage(id, "NULL NULL NULL NULL");

    }
}
