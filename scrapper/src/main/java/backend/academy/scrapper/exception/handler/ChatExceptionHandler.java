package backend.academy.scrapper.exception.handler;

import backend.academy.scrapper.dto.response.ApiErrorResponse;
import backend.academy.scrapper.exception.chat.ChatAlreadyExistsException;
import backend.academy.scrapper.exception.chat.ChatIllegalArgumentException;
import backend.academy.scrapper.exception.chat.ChatNotExistException;
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
public class ChatExceptionHandler {

    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ChatNotExistException.class)
    public ApiErrorResponse handlerException(ChatNotExistException ex) {
        log.error("ChatNotExistException: {}", ex.getMessage());
        return new ApiErrorResponse(
                "Некорректные параметры запроса",
                "BAD_REQUEST",
                ex.getClass().getName(),
                ex.getMessage(),
                Utils.getStackTrace(ex));
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ChatIllegalArgumentException.class)
    public ApiErrorResponse handlerException(ChatIllegalArgumentException ex) {
        log.error("ChatIllegalArgumentException: {}", ex.getMessage());
        return new ApiErrorResponse(
                "Некорректные параметры запроса",
                "BAD_REQUEST",
                ex.getClass().getName(),
                ex.getMessage(),
                Utils.getStackTrace(ex));
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ChatAlreadyExistsException.class)
    public ApiErrorResponse handlerException(ChatAlreadyExistsException ex) {
        log.error("ChatAlreadyExistsException: {}", ex.getMessage());
        return new ApiErrorResponse(
                "Некорректные параметры запроса",
                "BAD_REQUEST",
                ex.getClass().getName(),
                ex.getMessage(),
                Utils.getStackTrace(ex));
    }
}
