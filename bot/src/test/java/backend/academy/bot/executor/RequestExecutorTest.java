package backend.academy.bot.executor;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class RequestExecutorTest {

    @Test
    @DisplayName("RequestExecutor execute должен выкинуть исключение если telegramBot не задан")
    public void executeShouldThrowIllegalStateExceptionWhenTelegramBotNotSet() {
        TelegramBot telegramBot = null;
        RequestExecutor executor = new RequestExecutor(telegramBot);
        Assertions.assertThatThrownBy(() -> executor.execute(new SendMessage(1, "Testing")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("ТRequestExecutor должен выполнить запрос если telegramBot задан")
    public void executeShouldExecuteWhenTelegramBotSet() {
        TelegramBot mockTelegramBot = Mockito.mock(TelegramBot.class);
        RequestExecutor executor = new RequestExecutor(mockTelegramBot);
        executor.execute(new SendMessage(1, "Test message"));
        Mockito.verify(mockTelegramBot, Mockito.times(1)).execute(Mockito.any(SendMessage.class));
    }
}
