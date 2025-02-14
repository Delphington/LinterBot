package backend.academy.bot.api.exception;

public class ResponseException extends RuntimeException {
    public ResponseException(String message) {
        super(message);
    }
}
