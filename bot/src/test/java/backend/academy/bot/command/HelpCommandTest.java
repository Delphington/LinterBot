package backend.academy.bot.command;

import backend.academy.bot.command.helper.HelpCommand;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

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

    //    @Test
    //    @DisplayName("Проверка команды /help")
    //    void handle() {
    //        Update update = getMockUpdate(5L, "text");
    //        SendMessage sendMessage = helpCommand.handle(update);
    //        assertEquals(
    //                "/list -- Выводит список отслеживаемых ссылок\n" + "/start -- Начинает работу бота\n"
    //                        + "/track -- Добавляет ссылку для отслеживания\n"
    //                        + "/untrack -- Удаляет ссылку для отслеживания\n",
    //                sendMessage.getParameters().get("text"));
    //    }
}
