package backend.academy.scrapper.controller;

import backend.academy.scrapper.dto.request.filter.FilterRequest;
import backend.academy.scrapper.dto.response.filter.FilterListResponse;
import backend.academy.scrapper.dto.response.filter.FilterResponse;
import backend.academy.scrapper.service.AccessFilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.net.URI;

@RestController
@RequestMapping("/filter")
@Slf4j
@RequiredArgsConstructor
public class FilterController {

    private final AccessFilterService accessFilterService;

    @PostMapping("/{tgChatId}")
    @ResponseStatus(HttpStatus.CREATED)
    public FilterResponse createFilter(@PathVariable Long tgChatId, @RequestBody FilterRequest filterRequest) {
        log.info("POST /filter/{tgChatId}/create");


        // Имитация ошибок для тестирования (10% вероятность ошибки)

//        throw new WebClientResponseException(
//            503,
//            "Service Unavailable",
//            HttpHeaders.EMPTY,
//            null,
//            null
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
        return accessFilterService.deleteFilter(tgChatId, filterRequest);
    }
}
