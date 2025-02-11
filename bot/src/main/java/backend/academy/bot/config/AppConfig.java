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

    @Bean
    public TelegramBot telegramBot() {
        // Создаем кастомный Dispatcher с увеличенными лимитами
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(128); // Лимит одновременно выполняемых запросов
        dispatcher.setMaxRequestsPerHost(32); // Лимит запросов на один хост

        // Создаем кастомный OkHttpClient с нашим Dispatcher
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .build();

        // Передаем кастомный OkHttpClient в TelegramBot
        TelegramBot bot = new TelegramBot.Builder(botConfig.telegramToken())
            .okHttpClient(okHttpClient)
            .build();


        return bot;
    }
}
