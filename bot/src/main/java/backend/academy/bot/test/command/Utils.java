//package backend.academy.bot.test.command;
//
//import lombok.experimental.UtilityClass;
//import org.telegram.telegrambots.meta.api.objects.Message;
//import org.telegram.telegrambots.meta.api.objects.User;
//
//@UtilityClass
//public class Utils {
//
//    public static String getUserName(Message msg) {
//        return getUserName(msg.getFrom());
//    }
//
//    public static String getUserName(User user) {
//        return (user.getUserName() != null) ? user.getUserName() :
//            String.format("%s %s", user.getLastName(), user.getFirstName());
//    }
//}
