package backend.academy.bot.client.chat;

import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;

public interface ScrapperTgChatClient {

    void registerChat(final Long tgChatId);

    LinkResponse deleteChat(final Long tgChatId, final RemoveLinkRequest request);
}
