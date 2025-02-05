package backend.academy.bot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LinterTrackerBotConfig {

    @Bean
    public TelegramBot telegramBot(BotConfig botConfig){
        return new TelegramBot(botConfig.telegramToken());
    }
}
