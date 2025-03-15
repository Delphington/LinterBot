package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.request.tag.TagRemoveRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.dto.response.TagListResponse;

public interface TagService {
    ListLinksResponse getListLinkByTag(Long tgChatId, String tag);

    TagListResponse getAllListLinks(Long tgChatId);

    LinkResponse removeTagFromLink(Long tgChatId, TagRemoveRequest tagRemoveRequest);
}
