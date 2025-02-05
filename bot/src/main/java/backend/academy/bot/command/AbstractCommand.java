package backend.academy.bot.command;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class AbstractCommand {
    private final String command; // Название команды (например, "/start")
    private final String description; // Описание команды (для отображения в списке команд бота)

    // Метод для проверки, поддерживает ли команда указанный текст команды
    public boolean supports(String providedCommand) {
        return command.equals(providedCommand); // Возвращает true, если команда (this.command) совпадает с providedCommand
    }

    // Метод для преобразования команды в объект BotCommand из Telegram Bot API
    public BotCommand toApiCommand() {
        return new BotCommand(command, description); // Создает объект BotCommand с названием и описанием команды
    }

    // Абстрактный метод для обработки команды
    // Этот метод должен быть реализован в классах-наследниках
    public abstract SendMessage handle(Update update);
    // Update содержит всю информацию о входящем сообщении (текст, ID чата, и т.д.)
    // SendMessage - объект, который будет содержать ответ, который нужно отправить в Telegram
}
