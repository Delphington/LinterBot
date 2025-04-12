package backend.academy.scrapper.exception.filter;

public class AccessFilterNotExistException extends RuntimeException {
    public AccessFilterNotExistException(String message) {
        super(message);
    }
}
