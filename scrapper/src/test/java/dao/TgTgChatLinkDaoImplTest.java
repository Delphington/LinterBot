package dao;

import backend.academy.scrapper.dao.ChatLinkDao;
import backend.academy.scrapper.dao.chat.ChatDao;
import backend.academy.scrapper.dao.link.LinkDao;
import backend.academy.scrapper.dto.request.AddLinkRequest;
import base.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.util.List;

public class TgTgChatLinkDaoImplTest extends IntegrationTest {

    @Autowired
    private ChatLinkDao chatLinkDao;

    @Autowired
    private ChatDao chatDao;

    @Autowired
    private LinkDao linkDao;

    @DisplayName("Test: добавление записи в таблицу ChatLink")
    @Transactional
    @Test
    void addRecord() {
        Long chatId = 2L;
        chatDao.save(chatId);

        AddLinkRequest linkRequest = new AddLinkRequest(
            URI.create("https://github.com"),
            List.of("tag1", "tag2"),
            List.of("filter1", "filter2")
        );
        Long linkId = linkDao.addLink(linkRequest);

        chatLinkDao.addRecord(chatId, linkId);

        List<Long> linkIds = chatLinkDao.getLinkIdsByChatId(chatId);
        Assertions.assertEquals(1, linkIds.size());
        Assertions.assertTrue(linkIds.contains(linkId));
    }
}
