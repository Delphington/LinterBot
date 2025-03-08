package backend.academy.scrapper.exception.chat;

public class ChatNotExistException extends RuntimeException {
    public ChatNotExistException(String message) {
        super(message);
    }
}
