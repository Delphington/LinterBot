package backend.academy.scrapper.api.exception.handler;

import backend.academy.scrapper.api.dto.response.ApiErrorResponse;
import backend.academy.scrapper.api.exception.link.LinkAlreadyExistException;
import backend.academy.scrapper.api.exception.link.LinkNotFoundException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class LinkHandlerException {

    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "404",
            description = "Ссылка не найдена")
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(LinkNotFoundException.class)
    public ApiErrorResponse handlerException(LinkNotFoundException ex) {
        log.error("LinkNotFoundException: {}", ex.getMessage());
        return new ApiErrorResponse(
            "Ссылка не найдена",
            "NOT_FOUND",
            ex.getClass().getName(),
            ex.getMessage(),
            getStackTrace(ex)
        );
    }

    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "400",
            description = "Некорректные параметры запроса")
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LinkAlreadyExistException.class)
    public ApiErrorResponse handlerException(LinkAlreadyExistException ex) {
        log.error("LinkAlreadyExistException: {}", ex.getMessage());
        return new ApiErrorResponse(
            "Некорректные параметры запроса",
            "BAD_REQUEST",
            ex.getClass().getName(),
            ex.getMessage(),
            getStackTrace(ex)
        );
    }

    private List<String> getStackTrace(Exception ex) {
        return Arrays.stream(ex.getStackTrace())
            .map(StackTraceElement::toString)
            .toList();
    }
}
