package backend.academy.bot.api;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// Обработчик для преобразования исключений в ApiErrorResponse
@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {


    // Для аннотации Valid
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "400",
            description = "Некорректные параметры запроса")
    })
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Ошибка valid: {}", ex.getMessage());
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());

        ApiErrorResponse response = new ApiErrorResponse(
            "Некорректные параметры запроса",
            "VALIDATION_ERROR",
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    //Для обработки когда не можем преобразовать в JSON
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "400",
            description = "Некорректные параметры запроса")
    })
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleRuntimeErrors(HttpMessageNotReadableException ex) {
        log.error("Ошибка десериализации: {}", ex.getMessage());
        List<String> stacktrace = Arrays.stream(ex.getStackTrace())
            .map(StackTraceElement::toString)
            .toList();

        return new ApiErrorResponse(
            "Некорректные параметры запроса",
            "BAD_REQUEST",
            ex.getClass().getName(),
            ex.getMessage(),
            stacktrace
        );
    }


    //Обработка всех
    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handleException(Exception e) {
        log.error("общая ошибка: {}", e.getMessage());
        ApiErrorResponse response = new ApiErrorResponse(
            "Внутрення ошибка сервера",
            "INTERNAL_ERROR",
            e.getClass().getSimpleName(),
            e.getMessage(),
            Collections.emptyList()
        );
        return ResponseEntity.status(500).body(response);
    }
}
