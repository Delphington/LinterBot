package backend.academy.scrapper.controller;

import backend.academy.scrapper.dto.request.tag.TagLinkRequest;
import backend.academy.scrapper.dto.request.tag.TagRemoveRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.dto.response.TagListResponse;
import backend.academy.scrapper.service.TagService;
import backend.academy.scrapper.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{tgChatId}")
    public ListLinksResponse getListLinksByTag(@PathVariable("tgChatId") Long tgChatId,
                                               @RequestBody TagLinkRequest tagLinkRequest
    ) {
        log.error("Get links by tgChatId {} {}", Utils.sanitize(tgChatId), tagLinkRequest.toString());
        return tagService.getListLinkByTag(tgChatId, tagLinkRequest.tag());
    }

    @GetMapping("/{tgChatId}/all")
    public TagListResponse getAllListLinksByTag(@PathVariable("tgChatId") Long tgChatId) {
        log.info("getAllListLinksByTag: tgChatId={}", Utils.sanitize(tgChatId));
        return tagService.getAllListLinks(tgChatId);
    }

    @DeleteMapping("/{tgChatId}")
    public LinkResponse removeTagFromLink(@PathVariable("tgChatId") Long tgChatId,
                                          @RequestBody TagRemoveRequest tagRemoveRequest) {
        log.info("Remove tag link for tgChatId {} {}", Utils.sanitize(tgChatId), tagRemoveRequest.toString());
        return tagService.removeTagFromLink(tgChatId, tagRemoveRequest);
    }
}
