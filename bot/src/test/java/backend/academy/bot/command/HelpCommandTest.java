package backend.academy.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelpCommandTest extends BaseCommandTest {

    @Autowired
    private List<Command> commandList;
    @Autowired
    private HelpCommand helpCommand;

    @BeforeEach
    void setUp() {
        commandList.remove(helpCommand);
        commandList.add(helpCommand);
    }

    @Test
    @DisplayName("Проверка команды /help")
    void handle() {
        Update update = getMockUpdate(5L, "text");
        SendMessage sendMessage = helpCommand.handle(update);
        assertEquals("/list -- Выводит список отслеживаемых ссылок\n" +
            "/start -- Начинает работу бота\n" +
            "/track -- Добавляет ссылку для отслеживания\n" +
            "/untrack -- Удаляет ссылку для отслеживания\n", sendMessage.getParameters().get("text"));
    }
}
