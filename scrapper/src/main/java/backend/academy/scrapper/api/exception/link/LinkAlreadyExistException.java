package backend.academy.scrapper.api.exception.link;

public class LinkAlreadyExistException extends RuntimeException {
    public LinkAlreadyExistException(String message) {
        super(message);
    }
}
