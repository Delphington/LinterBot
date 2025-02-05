//package backend.academy.bot.test.command.service;
//
//import backend.academy.bot.config.BotConfig;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import java.util.Map;
//
//
//@RequiredArgsConstructor
//@Component
//@Slf4j
//public class TelegramBot extends TelegramLongPollingBot {
//
//    private final BotConfig botConfig;
//
//
//    private Map<Long, String> ml;
//
//    @Override
//    public void onUpdateReceived(Update update) {
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            String messageText = update.getMessage().getText();
//            long chatId = update.getMessage().getChatId(); // Используйте long
//            log.error("SЫЫЫЫ  " + messageText + "  " + chatId);
//            startCommandReceived(chatId, messageText);
//        }
//    }
//
//    //-------
//    private void startCommandReceived(long chatId, String name) {
//        String answer = "hi, " + name + ", nice to meet you"; // Добавлена запятая
//        sendMessage(chatId, answer);
//    }
//
//    private void sendMessage(long chatId, String text) { // Переименовано TextMessage в text
//        SendMessage sendMessage = new SendMessage(); // Создаем объект sendMessage
//        sendMessage.setChatId(String.valueOf(chatId)); // Преобразуем chatId в String
//        sendMessage.setText(text); // Устанавливаем текст сообщения
//        try {
//            execute(sendMessage);
//        } catch (TelegramApiException e) {
//        }
//    }
//
//    @Override
//    public String getBotUsername() {
//        return botConfig.getBotName();
//    }
//
//    @Override
//    public String getBotToken() {
//        return botConfig.getToken();
//    }
//}
