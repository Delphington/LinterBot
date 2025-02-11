package backend.academy.scrapper.api.exception.chat;

public class ChatNotExistException extends RuntimeException {
    public ChatNotExistException(String message) {
        super(message);
    }
}
