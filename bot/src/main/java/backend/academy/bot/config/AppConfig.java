package backend.academy.bot.config;

import com.pengrad.telegrambot.TelegramBot;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Configuration
public class AppConfig {

    private final BotConfig botConfig;
    private OkHttpClient okHttpClient;

    // Настройки пула потоков
    private static final int MAX_REQUEST = 128;
    private static final int MAX_REQUEST_PER_HOST = 32;
    private static final int CORE_POOL_SIZE = 16; // Базовое количество потоков
    private static final int MAX_POOL_SIZE = 64;  // Максимальное количество потоков
    private static final int KEEP_ALIVE_TIME = 60; // Время жизни неиспользуемых потоков (сек)
    private static final int QUEUE_CAPACITY = 1000; // Размер очереди задач

    @Bean
    public TelegramBot telegramBot() {

        // Создаем ThreadPoolExecutor с настраиваемыми параметрами
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(QUEUE_CAPACITY),
            new ThreadPoolExecutor.AbortPolicy()); // Политика отказа при переполнении

        // Настройка диспетчера OkHttp
        Dispatcher dispatcher = new Dispatcher(executor);
        dispatcher.setMaxRequests(MAX_REQUEST);
        dispatcher.setMaxRequestsPerHost(MAX_REQUEST_PER_HOST);

         okHttpClient = new OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .connectTimeout(30, TimeUnit.SECONDS) // Таймаут соединения
            .readTimeout(30, TimeUnit.SECONDS)    // Таймаут чтения
            .writeTimeout(30, TimeUnit.SECONDS)   // Таймаут записи
            .build();

        return new TelegramBot.Builder(botConfig.telegramToken())
            .okHttpClient(okHttpClient)
            .build();
    }

    @PreDestroy
    public void cleanup() {
        // При завершении работы приложения корректно закрываем ресурсы
        if (okHttpClient != null) {
            okHttpClient.dispatcher().executorService().shutdown();
            try {
                if (!okHttpClient.dispatcher().executorService().awaitTermination(5, TimeUnit.SECONDS)) {
                    okHttpClient.dispatcher().executorService().shutdownNow();
                }
            } catch (InterruptedException e) {
                okHttpClient.dispatcher().executorService().shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
