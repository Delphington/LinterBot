package backend.academy.scrapper.api.exception;

public class LinkAlreadyExistException extends RuntimeException {
    public LinkAlreadyExistException(String message) {
        super(message);
    }
}
