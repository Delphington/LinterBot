package backend.academy.scrapper.client.type;

import backend.academy.scrapper.tracker.update.model.LinkUpdate;

public interface UpdateSender {
    void sendUpdate(LinkUpdate linkUpdate);
}
