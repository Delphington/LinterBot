package dao;

import backend.academy.scrapper.dao.chat.TgChatDao;
import base.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class TgTgChatDaoImplTest extends IntegrationTest {

    @Autowired
    private TgChatDao tgChatDao;

    @DisplayName("Test: сохранения")
    @Transactional
    @Test
    void save() {
        Assertions.assertFalse(tgChatDao.isExistChat(1L));
        tgChatDao.save(1L);
        Assertions.assertTrue(tgChatDao.isExistChat(1L));
    }

    @DisplayName("Test: удаление чата")
    @Transactional
    @Test
    void remove() {
        tgChatDao.save(3L);
        Assertions.assertTrue(tgChatDao.isExistChat(3L));
        tgChatDao.remove(3L);
        Assertions.assertFalse(tgChatDao.isExistChat(3L));
    }

    @DisplayName("Test: удаление несуществующего чата")
    @Transactional
    @Test
    void removeNonExistentChat() {
        Assertions.assertFalse(tgChatDao.isExistChat(4L));
        tgChatDao.remove(4L);
        Assertions.assertFalse(tgChatDao.isExistChat(4L));
    }
}
