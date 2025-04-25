package backend.academy.bot.client.tag;

import backend.academy.bot.api.dto.request.tag.TagLinkRequest;
import backend.academy.bot.api.dto.request.tag.TagRemoveRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.api.dto.response.TagListResponse;

public interface ScrapperTagClient {
    ListLinksResponse getListLinksByTag(Long tgChatId, TagLinkRequest tagLinkRequest);

    TagListResponse getAllListLinksByTag(Long tgChatId);

    LinkResponse removeTag(Long tgChatId, TagRemoveRequest tg);
}
