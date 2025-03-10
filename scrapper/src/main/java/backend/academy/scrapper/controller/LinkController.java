package backend.academy.scrapper.controller;

import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.dto.request.RemoveLinkRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.service.orm.OrmLinkService;
import backend.academy.scrapper.util.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;

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
        log.info("LinkController getAllLinks {} ", Utils.sanitize(tgChatId));
        return linkService.getAllLinks(tgChatId);
    }

    @Operation(summary = "Добавить отслеживание ссылки")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена")})
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{tgChatId}")
    public LinkResponse addLink(@RequestHeader(value = "Tg-Chat-Id") Long tgChatId,
                                @RequestBody AddLinkRequest addLinkRequest) {
        log.info("LinkController addLink {}", Utils.sanitize(tgChatId));
        return linkService.addLink(tgChatId, addLinkRequest);
    }

    @Operation(summary = "Убрать отслеживание ссылки")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ссылка успешно убрана")})
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{tgChatId}")
    public LinkResponse deleteLink(
        @RequestHeader(value = "Tg-Chat-Id") Long tgChatId,
        @RequestBody @Valid RemoveLinkRequest removeLinkRequest) {
        log.info("LinkController deleteLink {}", Utils.sanitize(tgChatId));
        return linkService.deleteLink(tgChatId, removeLinkRequest.link());
    }


    @GetMapping("/by-tag")
    public ListLinksResponse getListLinkByTag(
        @RequestHeader("Tg-Chat-Id") Long tgChatId,
        @RequestParam String tag
    ) {
        log.info("LinkController getListLinkByTag  TAGS {}", tag);
        return linkService.getListLinkByTag(tgChatId, tag);
    }
}
