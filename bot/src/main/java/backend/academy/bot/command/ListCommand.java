package backend.academy.bot.command;

import backend.academy.bot.api.ScrapperClient;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.api.exception.ResponseException;
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

        Long id = update.message().chat().id();
        userStateManager.setUserStatus(update.message().chat().id(), UserState.WAITING_COMMAND);

        ListLinksResponse listLink;
        try {
            listLink = scrapperClient.getListLink(id);
        } catch (ResponseException e) {
            log.warn(
                    "Ошибка при формирование всех ссылок {}",
                    update.message().chat().id());
            return new SendMessage(id, "Ошибка при получении ссылок");
        }
        log.info("выполнилась команда /list {}", update.message().chat().id());

        if (listLink.size() == 0) {
            return new SendMessage(update.message().chat().id(), "Никакие ссылки еще не отслеживаются");
        }
        return new SendMessage(update.message().chat().id(), createMessage(listLink.links()));
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
