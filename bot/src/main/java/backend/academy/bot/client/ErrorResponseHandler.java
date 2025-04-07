package backend.academy.bot.client;

import backend.academy.bot.api.exception.ResponseException;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public class ErrorResponseHandler {
    private static final Logger log = LoggerFactory.getLogger(ErrorResponseHandler.class);

    public static Function<ClientResponse, Mono<? extends Throwable>> handleClientError(String operation) {
        return response -> createError(response, operation, true);
    }

    public static Function<ClientResponse, Mono<? extends Throwable>> handleServerError(String operation) {
        return response -> createError(response, operation, false);
    }

    private static Mono<? extends Throwable> createError(
            ClientResponse response, String operation, boolean isClientError) {
        return response.bodyToMono(String.class).flatMap(errorBody -> {
            String errorType = isClientError ? "Ошибка" : "Серверная ошибка";
            String errorMessage =
                    String.format("%s при %s: %s, Body: %s", errorType, operation, response.statusCode(), errorBody);
            log.error(errorMessage);
            return Mono.error(new ResponseException(errorMessage));
        });
    }
}
