package backend.academy.bot.command;

import backend.academy.bot.api.ScrapperClient;
import backend.academy.bot.api.dto.request.AddLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import backend.academy.bot.state.UserState;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    @SuppressWarnings({"ReturnCount", "MissingSwitchDefault"})
    public SendMessage handle(Update update) {
        Long id = update.message().chat().id();

        switch (userStateManager.getUserState(id)) {
            case WAITING_COMMAND, WAITING_URL -> {
                return getUrlMessage(update);
            }

            case WAITING_TAGS -> {
                return getTagsMessage(update);
            }
            case WAITING_FILTERS -> {

                //Инициализируем теги
                try {
                    List<String> listFilters = parserMessage.getAdditionalAttribute(update.message().text().trim());
                    userStateManager.addUserFilters(id, listFilters);
                } catch (InvalidInputFormatException e) {
                    log.warn("Пользователь не ввел фильтр {}", update.message().chat().id());
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
                    log.warn("Пользователь пытается добавить существующую ссылку: {}", update.message().chat().id());
                    return new SendMessage(id, "Такая ссылка уже добавлена, добавьте новую ссылку используя /track");
                }

                String stringLog = String.format("Ссылка добавлена!%nURL: %s%ntags: %s%nfilters: %s",
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
            log.warn("Ошибка при получении тегов {}", update.message().chat().id());
            return new SendMessage(id, e.getMessage());
        }

        userStateManager.addUserTags(id, listTags);
        userStateManager.setUserStatus(id, UserState.WAITING_FILTERS);
        log.info("Теги получены успешно {}", update.message().chat().id());
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
            return new SendMessage(id, e.getMessage());
        }

        userStateManager.setUserStatus(id, UserState.WAITING_TAGS);
        userStateManager.addUserURI(id, uri);
        log.info("Url пользователь ввел верно {}", update.message().chat().id());
        return new SendMessage(id, "Введите теги через пробел для ссылки");
    }

}
