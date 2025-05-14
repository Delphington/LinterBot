package backend.academy.scrapper.client;

import backend.academy.scrapper.tracker.update.model.LinkUpdate;

public interface TgBotClient {
    void sendUpdate(LinkUpdate linkUpdate);
}
