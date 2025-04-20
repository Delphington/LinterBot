package datebase.dao;

import backend.academy.scrapper.dao.chat.TgChatDaoImpl;
import datebase.TestDatabaseContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(classes = {
    DataSourceAutoConfiguration.class,
    JdbcTemplateAutoConfiguration.class,
    TgChatDaoImpl.class
})
public class TgChatDaoImplTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestDatabaseContainer.configureProperties(registry);
    }

    @Autowired
    private TgChatDaoImpl tgChatDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long tgChatId;
    private Long linkId;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM filters");
        jdbcTemplate.update("DELETE FROM tags");
        jdbcTemplate.update("DELETE FROM access_filter");
        jdbcTemplate.update("DELETE FROM tg_chat_links");
        jdbcTemplate.update("DELETE FROM links");
        jdbcTemplate.update("DELETE FROM tg_chats");


        tgChatId = 1L;
        linkId = 1L;

        jdbcTemplate.update("INSERT INTO tg_chats (id, created_at) VALUES (?, NOW())", tgChatId);
        jdbcTemplate.update(
            "INSERT INTO links (id, url, updated_at) VALUES (?, ?, NOW())",
            linkId, "https://example.com");
        jdbcTemplate.update(
            "INSERT INTO tg_chat_links (tg_chat_id, link_id) VALUES (?, ?)",
            tgChatId, linkId);
    }

    @Test
    @DisplayName("Test: сохранение чата")
    void save() {
        Long chatId = 2L;
        tgChatDao.save(chatId);
        Boolean exists = jdbcTemplate.queryForObject(
            "SELECT EXISTS (SELECT 1 FROM tg_chats WHERE id = ?)", Boolean.class, chatId);
        Assertions.assertTrue(exists != null && exists);
    }

    @Test
    @DisplayName("Test: удаление чата")
    void remove() {
        Long chatId = 2L;
        tgChatDao.save(chatId);
        tgChatDao.remove(chatId);
        Boolean exists = jdbcTemplate.queryForObject(
            "SELECT EXISTS (SELECT 1 FROM tg_chats WHERE id = ?)", Boolean.class, chatId);
        Assertions.assertFalse(exists != null && exists);
    }

    @Test
    @DisplayName("Test: удаление несуществующего чата")
    void remove_NonExistent() {
        Long chatId = 2L;
        tgChatDao.remove(chatId);

        Boolean exists = jdbcTemplate.queryForObject(
            "SELECT EXISTS (SELECT 1 FROM tg_chats WHERE id = ?)", Boolean.class, chatId);
        Assertions.assertFalse(exists != null && exists);
    }
}
