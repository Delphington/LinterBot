package dao;

import backend.academy.scrapper.dao.chat.TgChatDao;
import base.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

public class TgChatDaoImplTest extends IntegrationTest {

    @Autowired
    private TgChatDao tgChatDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long tgChatId;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM tg_chats");
        tgChatId = 1L;
    }

    @DisplayName("Test: сохранение чата")
    @Transactional
    @Test
    void save() {
        tgChatDao.save(tgChatId);
        Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM tg_chats WHERE id = ?)", Boolean.class, tgChatId);
        Assertions.assertTrue(exists != null && exists);
    }

    @DisplayName("Test: удаление чата")
    @Transactional
    @Test
    void remove() {
        tgChatDao.save(tgChatId);
        tgChatDao.remove(tgChatId);
        Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM tg_chats WHERE id = ?)", Boolean.class, tgChatId);
        Assertions.assertFalse(exists != null && exists);
    }

    @DisplayName("Test: удаление несуществующего чата")
    @Transactional
    @Test
    void remove_NonExistent() {

        tgChatDao.remove(tgChatId);

        Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM tg_chats WHERE id = ?)", Boolean.class, tgChatId);
        Assertions.assertFalse(exists != null && exists);
    }
}
