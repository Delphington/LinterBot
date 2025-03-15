package dao;

import backend.academy.scrapper.dao.TgChatLinkDao;
import backend.academy.scrapper.dao.chat.TgChatDao;
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

//public class TgTgTgChatLinkDaoImplTest extends IntegrationTest {
//
//    @Autowired
//    private TgChatLinkDao tgChatLinkDao;
//
//    @Autowired
//    private TgChatDao tgChatDao;
//
//    @Autowired
//    private LinkDao linkDao;
//
//    @DisplayName("Test: добавление записи в таблицу ChatLink")
//    @Transactional
//    @Test
//    void addRecord() {
//        Long chatId = 2L;
//        tgChatDao.save(chatId);
//
//        AddLinkRequest linkRequest = new AddLinkRequest(
//            URI.create("https://github.com"),
//            List.of("tag1", "tag2"),
//            List.of("filter1", "filter2")
//        );
//        Long linkId = linkDao.addLink(linkRequest);
//
//        tgChatLinkDao.addRecord(chatId, linkId);
//
//        List<Long> linkIds = tgChatLinkDao.getLinkIdsByChatId(chatId);
//        Assertions.assertEquals(1, linkIds.size());
//        Assertions.assertTrue(linkIds.contains(linkId));
//    }
//}
