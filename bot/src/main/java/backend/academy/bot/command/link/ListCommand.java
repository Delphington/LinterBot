package backend.academy.bot.command.link;

import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.command.Command;
import backend.academy.bot.redis.RedisCacheService;
import backend.academy.bot.state.UserState;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class ListCommand implements Command {

    private final ScrapperClient scrapperClient;
    private final UserStateManager userStateManager;

    private final RedisCacheService redisCacheService;

    @Override
    public String command() {
        return "/list";
    }

    @Override
    public String description() {
        return "Выводит список отслеживаемых ссылок";
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.message().chat().id();

        userStateManager.setUserStatus(chatId, UserState.WAITING_COMMAND);

        log.info("Написали команду");

        ListLinksResponse response;
        try {
            response = getLinks(chatId);
        } catch (ResponseException e) {
            log.error("Ошибка {}", e.getMessage());
            return new SendMessage(chatId.toString(), e.getMessage());
        }

        if (response.links().isEmpty()) {
            return new SendMessage(chatId.toString(), "Никакие ссылки не отслеживаются");
        }

        return new SendMessage(chatId.toString(), createMessage(response.links()));
    }

    private ListLinksResponse getLinks(Long chatId) {
        ListLinksResponse cached = redisCacheService.getCachedLinks(chatId);
        if (cached != null) {
            log.info("Достали ссылки из кэша");

            return cached;
        }
        log.info("Достали ссылки из БД");

        ListLinksResponse fresh = scrapperClient.getListLink(chatId);
        redisCacheService.cacheLinks(chatId, fresh);
        return fresh;
    }

    private String createMessage(List<LinkResponse> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("Отслеживаемые ссылки:\n");
        for (int i = 0; i < list.size(); i++) {
            sb.append(i + 1).append(")").append("\n");
            sb.append("URL:").append(list.get(i).url()).append("\n");
            sb.append("tags:").append(list.get(i).tags()).append("\n");
            sb.append("filters:").append(list.get(i).filters()).append("\n");
        }
        return sb.toString();
    }
}
