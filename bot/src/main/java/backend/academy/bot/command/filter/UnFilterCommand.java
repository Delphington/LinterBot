package backend.academy.bot.command.filter;

import backend.academy.bot.api.dto.request.filter.FilterRequest;
import backend.academy.bot.api.dto.response.filter.FilterResponse;
import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.exception.ServiceUnavailableCircuitException;
import backend.academy.bot.client.filter.ScrapperFilterClient;
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
public class UnFilterCommand implements Command {

    private final ScrapperFilterClient scrapperFilterClient;
    private final ParserMessage parserMessage;

    @Override
    public String command() {
        return "/unfilter";
    }

    @Override
    public String description() {
        return "Удаление фильтров";
    }

    @Override
    public SendMessage handle(Update update) {
        Long id = update.message().chat().id();
        String filterName;
        try {
            filterName = parserMessage.parseMessageFilter(
                    update.message().text().trim(), "Некорректный формат ввода. Ожидается: /unfilter filterName");
        } catch (InvalidInputFormatException e) {
            log.info("Не корректные поведение с /unfilter {}", id);
            return new SendMessage(id, e.getMessage());
        }

        FilterRequest filterRequest = new FilterRequest(filterName);

        try {
            FilterResponse filterResponse = scrapperFilterClient.deleteFilter(id, filterRequest);
            return new SendMessage(id, "фильтр успешно удален: " + filterResponse.filter());

        } catch (ResponseException e) {
            log.info("Ошибка добавления фильтра {}", id);
            return new SendMessage(id, "Ошибка: " + e.getMessage());
        } catch (ServiceUnavailableCircuitException e) {
            log.error("❌Service unavailable: {}", e.getMessage());
            return new SendMessage(
                    id, "⚠️ Сервис временно недоступен(Circuit). Пожалуйста, попробуйте через несколько минут.");
        } catch (Exception e) {
            return new SendMessage(id, "❌ Неизвестная ошибка при добавлении фильтра");
        }
    }
}
