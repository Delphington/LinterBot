package backend.academy.scrapper.client.tracker;

public class BadLinkRequestException extends RuntimeException {
    public BadLinkRequestException(String message) {
        super(message);
    }
}
