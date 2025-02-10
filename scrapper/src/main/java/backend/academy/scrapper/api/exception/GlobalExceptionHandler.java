package backend.academy.scrapper.api.exception;

import backend.academy.scrapper.api.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Arrays;
import java.util.List;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "400",
            description = "Некорректные параметры запроса")
    })
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrorResponse handleRuntimeErrors(MethodArgumentNotValidException ex) {
        log.error("FROM GlobalExceptionHandler MethodArgumentNotValidException");
        List<String> stacktrace = getStackTrace(ex);
        return new ApiErrorResponse(
            "Некорректные параметры запроса",
            "BAD_REQUEST",
            ex.getClass().getName(),
            ex.getMessage(),
            stacktrace
        );
    }

    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "400",
            description = "Некорректные параметры запроса")
    })
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiErrorResponse handleRuntimeErrors(HttpMessageNotReadableException ex) {
        log.error("FROM GlobalExceptionHandler HttpMessageNotReadableException");
        List<String> stacktrace = getStackTrace(ex);
        return new ApiErrorResponse(
            "Некорректные параметры запроса",
            "BAD_REQUEST",
            ex.getClass().getName(),
            ex.getMessage(),
            stacktrace
        );
    }

    private List<String> getStackTrace(Exception ex) {
        return Arrays.stream(ex.getStackTrace())
            .map(StackTraceElement::toString)
            .toList();
    }

}
