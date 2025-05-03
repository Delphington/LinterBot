package backend.academy.scrapper.controller;

import backend.academy.scrapper.dto.request.filter.FilterRequest;
import backend.academy.scrapper.dto.response.filter.FilterListResponse;
import backend.academy.scrapper.dto.response.filter.FilterResponse;
import backend.academy.scrapper.service.AccessFilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@RequestMapping("/filter")
@Slf4j
@RequiredArgsConstructor
public class FilterController {

    private final AccessFilterService accessFilterService;

    @PostMapping("/{tgChatId}")
    @ResponseStatus(HttpStatus.CREATED)
    public FilterResponse createFilter(@PathVariable Long tgChatId, @RequestBody FilterRequest filterRequest) {
        log.info("POST /filter/{tgChatId}");

        //        throw new HttpServerErrorException(
        //            HttpStatus.INTERNAL_SERVER_ERROR,
        //            "Сервер сломался по-настоящему"
        //        );

        //        throw new HttpClientErrorException(
        //            HttpStatus.NOT_FOUND,
        //            "Сервер сломался по-настоящему"
        //        );
        return accessFilterService.createFilter(tgChatId, filterRequest);
    }

    @GetMapping("/{tgChatId}")
    @ResponseStatus(HttpStatus.OK)
    public FilterListResponse getAllFilter(@PathVariable Long tgChatId) {
        log.info("GET /filter/{tgChatId}");
        return accessFilterService.getAllFilter(tgChatId);
    }

    @DeleteMapping("/{tgChatId}/delete")
    @ResponseStatus(HttpStatus.OK)
    public FilterResponse deleteFilter(@PathVariable Long tgChatId, @RequestBody FilterRequest filterRequest) {
        log.info("DELETE /filter/{tgChatId}/delete");
        // 70% вероятность исключения
        if (Math.random() < 0.5) {
            log.info("INTERNAL_SERVER_ERROR");

            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Серверная ошибка (тестовая, 70% вероятность)");
        }

        if (Math.random() < 0.5) {
            log.info("NOT_FOUND");

            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Сервер сломался по-настоящему");
        }
        log.info("ResponseException");

        return accessFilterService.deleteFilter(tgChatId, filterRequest);
    }
}
