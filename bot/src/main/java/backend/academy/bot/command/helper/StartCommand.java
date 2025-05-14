package backend.academy.bot.command.helper;

import backend.academy.bot.api.exception.ResponseException;
import backend.academy.bot.client.chat.ScrapperTgChatClient;
import backend.academy.bot.client.exception.ServiceUnavailableCircuitException;
import backend.academy.bot.command.Command;
import backend.academy.bot.state.UserState;
import backend.academy.bot.state.UserStateManager;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class StartCommand implements Command {

    private final ScrapperTgChatClient scrapperTgChatClient;
    private final UserStateManager userStateManager;

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return "Начинает работу бота";
    }

    @Override
    public SendMessage handle(Update update) {
        userStateManager.setUserStatus(update.message().chat().id(), UserState.WAITING_COMMAND);

        String message = "Привет! Используй /help чтобы увидеть все команды";
        try {
            scrapperTgChatClient.registerChat(update.message().chat().id());
        } catch (ResponseException e) {
            message = "Ты уже зарегистрировался :)";
            log.info(
                    "Не корректные поведение с регистрацией {}",
                    update.message().chat().id());
        } catch (ServiceUnavailableCircuitException e) {
            log.error("❌Service unavailable: {}", e.getMessage());
            return new SendMessage(
                    update.message().chat().id(),
                    "⚠️ Сервис временно недоступен(Circuit). Пожалуйста, попробуйте через несколько минут.");
        } catch (Exception e) {
            return new SendMessage(update.message().chat().id(), "❌ Неизвестная ошибка при добавлении фильтра");
        }
        log.info("выполнилась команда /start {}", update.message().chat().id());

        return new SendMessage(update.message().chat().id(), message);
    }
}
