package backend.academy.bot.client.link;

import backend.academy.bot.api.dto.request.AddLinkRequest;
import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;

public interface ScrapperLinkClient {
    LinkResponse trackLink(Long tgChatId, AddLinkRequest request);

    LinkResponse untrackLink(Long tgChatId, RemoveLinkRequest request);

    ListLinksResponse getListLink(Long tgChatId);
}
