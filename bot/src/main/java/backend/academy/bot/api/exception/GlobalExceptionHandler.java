package backend.academy.bot.api.exception;

import backend.academy.bot.api.dto.response.ApiErrorResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@SuppressWarnings("MultipleStringLiterals")
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        log.error("GlobalExceptionHandler: ОШИБКА valid: {}", ex.getMessage());

        return new ApiErrorResponse(
                "Некорректные параметры запроса",
                "VALIDATION_ERROR",
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                getStackTrace(ex));
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")})
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleSerializeException(HttpMessageNotReadableException ex) {
        log.error("Ошибка десcериализации: {}", ex.getMessage());
        List<String> stacktrace = getStackTrace(ex);
        return new ApiErrorResponse(
                "Некорректные параметры запроса", "BAD_REQUEST", ex.getClass().getName(), ex.getMessage(), stacktrace);
    }

    private List<String> getStackTrace(Exception ex) {
        return Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
    }
}
