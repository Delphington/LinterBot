package backend.academy.scrapper.exception.tag;

public class TagNotExistException extends RuntimeException {
    public TagNotExistException(String message) {
        super(message);
    }
}
