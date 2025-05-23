package backend.academy.scrapper.dao;

import java.util.List;

public interface TgChatLinkDao {

    List<Long> getLinkIdsByChatId(Long chatId);

    void addRecord(Long chatId, Long linkId);
}
