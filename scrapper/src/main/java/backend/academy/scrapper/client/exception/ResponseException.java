package backend.academy.scrapper.client.exception;

public class ResponseException extends RuntimeException {
    public ResponseException(String message) {
        super(message);
    }
}
