package backend.academy.scrapper.api;

import backend.academy.scrapper.api.dto.AddLinkRequest;
import backend.academy.scrapper.api.dto.LinkResponse;
import backend.academy.scrapper.api.dto.ListLinksResponse;
import backend.academy.scrapper.api.dto.RemoveLinkRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


@Log4j2
@RestController
@RequestMapping("/links")
public class LinkController {


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
        return null;
    }


    @Operation(summary = "Добавить отслеживание ссылки")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ссылка успешно добавлена"
        )
    })

    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public LinkResponse addLink(@RequestHeader(value = "Tg-Chat-Id", required = true) Long tgChatId,
                                @RequestBody AddLinkRequest addLinkRequest){
        log.error("HELLO FROM LinkController addLink chatId = " + tgChatId + " body = " + addLinkRequest);
        return null;
    }

    @Operation(summary = "Убрать отслеживание ссылки")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ссылка успешно убрана"
        )
    })

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public LinkResponse deleteLink(@RequestHeader(value = "Tg-Chat-Id", required = true) Long tgChatId,
    @RequestBody RemoveLinkRequest removeLinkRequest){
        log.error("HELLO FROM LinkController deleteLink chatId = " + tgChatId + " body = " + removeLinkRequest);
        return null;

    }



}
