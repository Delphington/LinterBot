package backend.academy.scrapper.tracker.update.exception.handler;

import backend.academy.scrapper.dto.response.ApiErrorResponse;
import backend.academy.scrapper.tracker.update.exception.BadLinkRequestException;
import backend.academy.scrapper.util.Utils;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
            Utils.getStackTrace(ex));
    }
}
