package backend.academy.scrapper.exception.link;

public class LinkAlreadyExistException extends RuntimeException {
    public LinkAlreadyExistException(String message) {
        super(message);
    }
}
