package backend.academy.scrapper.tracker;

import backend.academy.scrapper.tracker.update.model.LinkUpdate;

public interface TelegramBotClient {
     void addUpdate(LinkUpdate linkUpdate);
}
