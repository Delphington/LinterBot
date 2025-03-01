package backend.academy.scrapper.tracker.update;

import backend.academy.scrapper.api.dto.response.ApiErrorResponse;
import backend.academy.scrapper.tracker.update.exception.BadLinkRequestException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadLinkRequestException.class)
    public ApiErrorResponse handlerException(BadLinkRequestException ex) {
        log.error("BadLinkRequestException: {}", ex.getMessage());
        return new ApiErrorResponse(
                "Некорректные параметры запроса",
                "BAD_REQUEST",
                ex.getClass().getName(),
                ex.getMessage(),
                getStackTrace(ex));
    }

    private List<String> getStackTrace(Exception ex) {
        return Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
    }
}
