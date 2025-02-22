package backend.academy.scrapper.api.exception.handler;

import backend.academy.scrapper.api.dto.response.ApiErrorResponse;
import backend.academy.scrapper.api.exception.chat.ChatAlreadyExistsException;
import backend.academy.scrapper.api.exception.chat.ChatIllegalArgumentException;
import backend.academy.scrapper.api.exception.chat.ChatNotExistException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Arrays;
import java.util.List;

@Log4j2
@RestControllerAdvice
public class ChatHandlerException {


    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "400",
            description = "Некорректные параметры запроса")
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ChatNotExistException.class)
    public ApiErrorResponse handlerException(ChatNotExistException ex) {
        log.error("ChatNotExistException: {}", ex.getMessage());
        return new ApiErrorResponse(
            "Некорректные параметры запроса",
            "BAD_REQUEST",
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
    @ExceptionHandler(ChatIllegalArgumentException.class)
    public ApiErrorResponse handlerException(ChatIllegalArgumentException ex) {
        log.error("ChatIllegalArgumentException: {}", ex.getMessage());
        return new ApiErrorResponse(
            "Некорректные параметры запроса",
            "BAD_REQUEST",
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
    @ExceptionHandler(ChatAlreadyExistsException.class)
    public ApiErrorResponse handlerException(ChatAlreadyExistsException ex) {
        log.error("ChatAlreadyExistsException: {}", ex.getMessage());
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
