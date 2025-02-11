package backend.academy.scrapper.api.controller;

import backend.academy.scrapper.api.dto.request.AddLinkRequest;
import backend.academy.scrapper.api.dto.response.LinkResponse;
import backend.academy.scrapper.api.dto.response.ListLinksResponse;
import backend.academy.scrapper.api.dto.request.RemoveLinkRequest;
import backend.academy.scrapper.api.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

//todo: заглушки убрать

@RequiredArgsConstructor
@Log4j2
@RestController
@RequestMapping("/links")
public class LinkController {

    private final LinkService linkService;


    @Operation(summary = "Получить все отслеживаемые ссылки")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ссылки успешно получены"
        )
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ListLinksResponse getAllLinks(@RequestHeader(value = "Tg-Chat-Id", required = true) Long tgChatId) {
        //component
        log.error("HELLO FROM LinkController getAllLinks chatId = " + tgChatId);
        return linkService.getAllLinks(tgChatId);
    }

    /// ----------------------------------------
    @Operation(summary = "Добавить отслеживание ссылки")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ссылка успешно добавлена"
        )
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{tgChatId}")
    public LinkResponse addLink(@RequestHeader(value = "Tg-Chat-Id", required = true) Long tgChatId,
                                @RequestBody AddLinkRequest addLinkRequest) {

        return linkService.addLink(tgChatId, addLinkRequest);
    }

    @Operation(summary = "Убрать отслеживание ссылки")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ссылка успешно убрана"
        )
    })

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{tgChatId}")
    public LinkResponse deleteLink(@RequestHeader(value = "Tg-Chat-Id", required = true) Long tgChatId,
                                   @RequestBody @Valid RemoveLinkRequest removeLinkRequest) {
        log.error("HELLO FROM LinkController deleteLink chatId = " + tgChatId + " body = " + removeLinkRequest);

        return  linkService.deleteLink(tgChatId, removeLinkRequest.link());
    }
}
