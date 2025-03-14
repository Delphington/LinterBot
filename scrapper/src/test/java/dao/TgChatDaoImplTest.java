package dao;

import backend.academy.scrapper.dao.chat.ChatDao;
import base.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class TgChatDaoImplTest extends IntegrationTest {

    @Autowired
    private ChatDao chatDao;

    @DisplayName("Test: сохранения")
    @Transactional
    @Test
    void save() {
        Assertions.assertFalse(chatDao.isExistChat(1L));
        chatDao.save(1L);
        Assertions.assertTrue(chatDao.isExistChat(1L));
    }

    @DisplayName("Test: удаление чата")
    @Transactional
    @Test
    void remove() {
        chatDao.save(3L);
        Assertions.assertTrue(chatDao.isExistChat(3L));
        chatDao.remove(3L);
        Assertions.assertFalse(chatDao.isExistChat(3L));
    }

    @DisplayName("Test: удаление несуществующего чата")
    @Transactional
    @Test
    void removeNonExistentChat() {
        Assertions.assertFalse(chatDao.isExistChat(4L));
        chatDao.remove(4L);
        Assertions.assertFalse(chatDao.isExistChat(4L));
    }
}
