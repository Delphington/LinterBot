package backend.academy.scrapper.exception.handler;

import backend.academy.scrapper.dto.response.ApiErrorResponse;
import backend.academy.scrapper.exception.filter.AccessFilterAlreadyExistException;
import backend.academy.scrapper.exception.filter.AccessFilterNotExistException;
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
public class AccessFilterExceptionHandler {

    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "Такой фильтр уже существует")})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccessFilterAlreadyExistException.class)
    public ApiErrorResponse handlerException(AccessFilterAlreadyExistException ex) {
        log.error("AccessFilterAlreadyExistException: {}", ex.getMessage());
        return new ApiErrorResponse(
                "Такой фильтр уже существует",
                "BAD_REQUEST",
                ex.getClass().getName(),
                ex.getMessage(),
                Utils.getStackTrace(ex));
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "Такого фильтра нет")})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccessFilterNotExistException.class)
    public ApiErrorResponse handlerException(AccessFilterNotExistException ex) {
        log.error("AccessFilterNotExistException: {}", ex.getMessage());
        return new ApiErrorResponse(
                "Такого фильтра нет", "BAD_REQUEST", ex.getClass().getName(), ex.getMessage(), Utils.getStackTrace(ex));
    }
}
