//package backend.academy.bot.config;
//
//import backend.academy.bot.test.command.service.TelegramBot;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.meta.TelegramBotsApi;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
//
//@Component
//public class BotInitializer {
//
//    private final TelegramBot bot;
//
//    @Autowired // Хотя это необязательно с одним конструктором
//    public BotInitializer(TelegramBot bot) {
//        this.bot = bot;
//    }
//
//    @EventListener(ContextRefreshedEvent.class)
//    public void init() {
//        try {
//            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
//            telegramBotsApi.registerBot(bot);
//        } catch (TelegramApiException e) {
//        }
//    }
//
//}
