package backend.academy.bot.executor;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RequestExecutor {

    private final TelegramBot telegramBot;

    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(BaseRequest<T, R> request) {
        if (telegramBot == null) {
            throw new IllegalStateException("Telegram bot is not working");
        }
        telegramBot.execute(request);
    }
}
