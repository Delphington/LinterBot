package backend.academy.bot.command.filter;

import backend.academy.bot.api.dto.request.filter.FilterRequest;
import backend.academy.bot.api.dto.response.filter.FilterListResponse;
import backend.academy.bot.api.dto.response.filter.FilterResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.command.Command;
import backend.academy.bot.exception.InvalidInputFormatException;
import backend.academy.bot.message.ParserMessage;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilterListCommand implements Command {

    private final ScrapperClient scrapperClient;
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
        String filterName;

        try {
            FilterListResponse filterListResponse = scrapperClient.getFilterList(id);
            log.info("Мы получили ответ от backend");
            return new SendMessage(id, "ок: " +  filterListResponse.toString());
        } catch (ResponseException e) {
            log.info("бэк вернул ошибку");
            return new SendMessage(id, "Ошибка: " + e.getMessage());
        }
    }
}
