package backend.academy.bot.command.filter;

import backend.academy.bot.api.dto.request.filter.FilterRequest;
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
public class FilterCommand implements Command {

    private final ScrapperClient scrapperClient;
    private final ParserMessage parserMessage;

    @Override
    public String command() {
        return "/filter";
    }

    @Override
    public String description() {
        return "Позволяет добавить фильтрацию на получение уведомлений";
    }

    @Override
    public SendMessage handle(Update update) {

        Long id = update.message().chat().id();
        String filterName;
        try {
            filterName = parserMessage.parseMessageFilter(
                    update.message().text().trim(), "Некорректный формат ввода. Ожидается: /filter filterName");
        } catch (InvalidInputFormatException e) {
            log.info("Не корректные поведение с /filter {}", id);
            return new SendMessage(id, e.getMessage());
        }

        FilterRequest filterRequest = new FilterRequest(filterName);

        try {
            scrapperClient.createFilter(id, filterRequest);
            return new SendMessage(id, "Фильтр успешно добавлен");
        } catch (ResponseException e) {
            log.info("Ошибка добавления фильтра: {}", e.getMessage());
            return new SendMessage(id, "Ошибка: такой фильтр уже существует");
        }
    }
}
