package backend.academy.bot.api;

public class ResponseException extends RuntimeException {
    public ResponseException(String message) {
        super(message);
    }
}
