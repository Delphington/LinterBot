package backend.academy.bot.config;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class AppConfig {

    private final BotConfig botConfig;

    private final static int MAX_REQUEST = 128;
    private final static int MAX_REQUEST_PER_HOST = 32;

    @Bean
    public TelegramBot telegramBot() {

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(MAX_REQUEST); // Лимит одновременно выполняемых запросов
        dispatcher.setMaxRequestsPerHost(MAX_REQUEST_PER_HOST);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .build();

        TelegramBot bot = new TelegramBot.Builder(botConfig.telegramToken())
                .okHttpClient(okHttpClient)
                .build();

        return bot;
    }
}
