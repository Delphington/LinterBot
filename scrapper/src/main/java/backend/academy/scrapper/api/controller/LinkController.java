package backend.academy.scrapper.api.controller;

import backend.academy.scrapper.api.dto.request.AddLinkRequest;
import backend.academy.scrapper.api.dto.request.RemoveLinkRequest;
import backend.academy.scrapper.api.dto.response.LinkResponse;
import backend.academy.scrapper.api.dto.response.ListLinksResponse;
import backend.academy.scrapper.api.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/links")
public class LinkController {

    private final LinkService linkService;

    @Operation(summary = "Получить все отслеживаемые ссылки")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ссылки успешно получены")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ListLinksResponse getAllLinks(@RequestHeader(value = "Tg-Chat-Id") Long tgChatId) {
        log.info("LinkController getAllLinks {} ", tgChatId);
        return linkService.getAllLinks(tgChatId);
    }

    @Operation(summary = "Добавить отслеживание ссылки")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена")})
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{tgChatId}")
    public LinkResponse addLink(
            @RequestHeader(value = "Tg-Chat-Id") Long tgChatId, @RequestBody AddLinkRequest addLinkRequest) {
        log.info("LinkController addLink {} {} ", tgChatId, addLinkRequest);
        return linkService.addLink(tgChatId, addLinkRequest);
    }

    @Operation(summary = "Убрать отслеживание ссылки")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ссылка успешно убрана")})
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{tgChatId}")
    public LinkResponse deleteLink(
            @RequestHeader(value = "Tg-Chat-Id") Long tgChatId,
            @RequestBody @Valid RemoveLinkRequest removeLinkRequest) {
        log.info("LinkController deleteLink {} {} ", tgChatId, removeLinkRequest);
        return linkService.deleteLink(tgChatId, removeLinkRequest.link());
    }
}
