package backend.academy.bot.client.exception;

public class ServiceUnavailableCircuitException extends RuntimeException{
    public ServiceUnavailableCircuitException(String message) {
        super(message);
    }
}
