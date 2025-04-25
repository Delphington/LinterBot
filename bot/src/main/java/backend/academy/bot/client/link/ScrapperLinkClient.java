package backend.academy.bot.client.link;

import backend.academy.bot.api.dto.request.AddLinkRequest;
import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;

public interface ScrapperLinkClient {
    LinkResponse trackLink(final Long tgChatId, final AddLinkRequest request);

    LinkResponse untrackLink(final Long tgChatId, final RemoveLinkRequest request);

    ListLinksResponse getListLink(final Long tgChatId);
}
