package backend.academy.bot.command.filter;

import backend.academy.bot.api.dto.response.filter.FilterListResponse;
import backend.academy.bot.api.dto.response.filter.FilterResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.exception.ServiceUnavailableCircuitException;
import backend.academy.bot.client.filter.ScrapperFilterClient;
import backend.academy.bot.command.Command;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilterListCommand implements Command {

    private final ScrapperFilterClient scrapperFilterClient;
    private final ParserMessage parserMessage;

    @Override
    public String command() {
        return "/filterlist";
    }

    @Override
    public String description() {
        return "Выводи все фильтры";
    }

    @Override
    public SendMessage handle(Update update) {
        Long id = update.message().chat().id();

        try {
            parserMessage.parseMessageFilterList(update.message().text().trim());
        } catch (InvalidInputFormatException e) {
            log.info("Ошибка ввода /filterlist");
            return new SendMessage(id, "Ошибка: " + e.getMessage());
        }

        try {
            FilterListResponse filterListResponse = scrapperFilterClient.getFilterList(id);
            log.info("Мы получили ответ от backend");
            return new SendMessage(id, createMessage(filterListResponse.filterList()));
        } catch (ResponseException e) {
            log.info("бэк вернул ошибку");
            return new SendMessage(id, "Ошибка: " + e.getMessage());
        } catch (ServiceUnavailableCircuitException e) {
            log.error("❌Service unavailable: {}", e.getMessage());
            return new SendMessage(
                    id, "⚠️ Сервис временно недоступен(Circuit). Пожалуйста, попробуйте через несколько минут.");
        } catch (Exception e) {
            return new SendMessage(id, "❌ Неизвестная ошибка при добавлении фильтра");
        }
    }

    private String createMessage(List<FilterResponse> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("Фильтры blackList:\n");
        for (int i = 0; i < list.size(); i++) {
            sb.append(i + 1).append(") ").append(list.get(i).filter()).append("\n");
        }
        return sb.toString();
    }
}
