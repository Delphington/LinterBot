package backend.academy.bot.command;

import backend.academy.bot.executor.RequestExecutor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Component
public class Commands {

    private List<Command> commandList;

    @Autowired
    public Commands(ApplicationContext applicationContext) {
        // Получаем все бины типа Command из ApplicationContext
        Map<String, Command> commandBeans = applicationContext.getBeansOfType(Command.class);
        commandList = commandBeans.values().stream().collect(Collectors.toList());
    }
}
