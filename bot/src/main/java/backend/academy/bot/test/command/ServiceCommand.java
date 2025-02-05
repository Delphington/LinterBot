//package backend.academy.bot.command;
//
//
//import lombok.extern.slf4j.Slf4j;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
//import org.telegram.telegrambots.meta.bots.AbsSender;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//@Slf4j
//abstract class ServiceCommand extends BotCommand {
//
//    public ServiceCommand(String command, String description) {
//        super(command, description);
//    }
//
//
//    void sendAnswer(AbsSender absSender, Long chatId, String commandName,
//                    String userName, String text){
//        SendMessage sendMessage =new SendMessage();
//        sendMessage.enableMarkdown(true);
//        sendMessage.setChatId(chatId);
//        sendMessage.setText(text);
//        try{
//            absSender.execute(sendMessage);
//        } catch (TelegramApiException e) {
//            log.error(getClass().getName(), "error: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}
