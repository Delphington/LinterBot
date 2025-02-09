package backend.academy.bot.api.exception;

import backend.academy.bot.api.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Обработчик для преобразования исключений в ApiErrorResponse
@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Для аннотации Valid
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "400",
            description = "Некорректные параметры запроса")
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        log.error("GlobalExceptionHandler: ОШИБКА valid: {}", ex.getMessage());

        return new ApiErrorResponse(
            "Некорректные параметры запроса",
            "VALIDATION_ERROR",
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            getStackTrace(ex)
        );
    }

    //Для обработки когда не можем преобразовать в JSON
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "400",
            description = "Некорректные параметры запроса")
    })
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleSerializeException(HttpMessageNotReadableException ex) {
        log.error("Ошибка десcериализации: {}", ex.getMessage());
        List<String> stacktrace = getStackTrace(ex);

        return new ApiErrorResponse(
            "Некорректные параметры запроса для cериализации",
            "BAD_REQUEST",
            ex.getClass().getName(),
            ex.getMessage(),
            stacktrace
        );
    }


    //=========================================================
    //------------- Нету в openAPI  --------------------------
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "500",
            description = "Ошибки")
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ApiErrorResponse handleException(RuntimeException e) {
        log.error("ОБЩАЯ ошибка: {}", e.getMessage());
        return new ApiErrorResponse(
            "Внутрення ошибка сервера",
            "INTERNAL_ERROR",
            e.getClass().getSimpleName(),
            e.getMessage(),
            Collections.emptyList()
        );
    }

    private List<String> getStackTrace(Exception ex) {
        return Arrays.stream(ex.getStackTrace())
            .map(StackTraceElement::toString)
            .toList();
    }
}
