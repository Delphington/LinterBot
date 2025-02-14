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


    public SendMessage handle(Update update) {
        Long id = update.message().chat().id();

        switch (userStateManager.getUserState(id)) {
            case WAITING_COMMAND, WAITING_URL -> getUrlMessage(update);
            case WAITING_TAGS -> getTagsMessage(update);
            case WAITING_FILTERS -> {

                //Инициализируем теги
                try {
                    List<String> listFilters = parserMessage.getAdditionalAttribute(update.message().text().trim());
                    userStateManager.addUserFilters(id, listFilters);
                } catch (InvalidInputFormatException e) {
                    log.error("Пользователь: не ввел фильтр");
                    return new SendMessage(id, e.getMessage());
                }

                // работаем со всеми введенными данными
                AddLinkRequest addLinkRequest = new AddLinkRequest(userStateManager.getURIByUserId(id),
                    userStateManager.getListTagsByUserId(id), userStateManager.getListFiltersByUserId(id));

                LinkResponse linkResponse;
                try {
                    linkResponse = scrapperClient.trackLink(id, addLinkRequest);
                } catch (ResponseException e) {
                    clear(id);
                    log.error("Пользователь пытается добавить существующую ссылку: {}", e.getMessage());
                    return new SendMessage(id, "Такая ссылка уже добавлена, добавьте новую ссылку используя /track");
                }

                String stringLog = String.format("Ссылка добавлена!\nURL: %s\ntags: %s\nfilters: %s",
                    linkResponse.url(), linkResponse.tags(), linkResponse.filters());
                clear(id);
                return new SendMessage(id, stringLog);
            }
        }
        return new SendMessage(id, "Попробуй добавить новую ссылку");
    }


    private SendMessage getTagsMessage(Update update) {
        Long id = update.message().chat().id();

        List<String> listTags;
        try {
            listTags = parserMessage.getAdditionalAttribute(update.message().text().trim());
        } catch (InvalidInputFormatException e) {
            log.error("Пользователь: не ввел теги");
            return new SendMessage(id, e.getMessage());
        }

        userStateManager.addUserTags(id, listTags);
        userStateManager.setUserStatus(id, UserState.WAITING_FILTERS);

        return new SendMessage(id, "Введите фильтры через пробел для ссылки");
    }

    private void clear(Long id) {
        userStateManager.clearUserStates(id);
        userStateManager.clearUserInfoLinkMap(id);
    }


    private SendMessage getUrlMessage(Update update) {
        Long id = update.message().chat().id();
        URI uri;

        try {
            uri = parserMessage.parseUrl(update.message().text().trim(),
                userStateManager.getUserState(id));
        } catch (InvalidInputFormatException e) {
            userStateManager.setUserStatus(id, UserState.WAITING_URL);
            log.error("Пользователь: неверно ввел /track, статус -> WAITING_URL");
            return new SendMessage(id, e.getMessage());
        }

        userStateManager.setUserStatus(id, UserState.WAITING_TAGS);
        userStateManager.addUserURI(id, uri);

        return new SendMessage(id, "Введите теги через пробел для ссылки");
    }

}
