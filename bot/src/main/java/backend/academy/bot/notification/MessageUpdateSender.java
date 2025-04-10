package backend.academy.bot.notification;

import backend.academy.bot.api.dto.request.LinkUpdate;
import backend.academy.bot.executor.RequestExecutor;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageUpdateSender {

    private final RequestExecutor execute;

    public void sendMessage(LinkUpdate linkUpdate) {
        for (Long chatId : linkUpdate.tgChatIds()) {
            SendMessage sendMessage = new SendMessage(
                chatId, String.format("Обновление по ссылке: %s%n %s", linkUpdate.url(), linkUpdate.description()));
            execute.execute(sendMessage);
        }
    }
}
