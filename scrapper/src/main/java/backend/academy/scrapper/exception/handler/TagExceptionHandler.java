package backend.academy.scrapper.exception.handler;

import backend.academy.scrapper.dto.response.ApiErrorResponse;
import backend.academy.scrapper.exception.tag.TagNotExistException;
import backend.academy.scrapper.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class TagExceptionHandler {

    @ExceptionHandler(TagNotExistException.class)
    public ApiErrorResponse handlerException(TagNotExistException ex) {
        log.error("TagNotExistException: {}", ex.getMessage());
        return new ApiErrorResponse(
                "Тег не найден", "NOT_FOUND", ex.getClass().getName(), ex.getMessage(), Utils.getStackTrace(ex));
    }
}
