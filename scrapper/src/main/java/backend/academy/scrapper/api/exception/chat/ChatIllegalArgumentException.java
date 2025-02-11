package backend.academy.scrapper.api.exception.chat;

public class ChatIllegalArgumentException extends RuntimeException {
    public ChatIllegalArgumentException(String message) {
        super(message);
    }
}
