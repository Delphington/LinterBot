package backend.academy.bot.client.chat;

import backend.academy.bot.api.dto.request.RemoveLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;

public interface ScrapperTgChatClient {

    void registerChat(Long tgChatId);

    LinkResponse deleteChat(Long tgChatId, RemoveLinkRequest request);
}
