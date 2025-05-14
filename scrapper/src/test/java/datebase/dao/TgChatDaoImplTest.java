package datebase.dao;

import backend.academy.scrapper.dao.chat.TgChatDaoImpl;
import datebase.TestDatabaseContainerDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(classes = {DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, TgChatDaoImpl.class})
public class TgChatDaoImplTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestDatabaseContainerDao.configureProperties(registry);
    }

    @Autowired
    private TgChatDaoImpl tgChatDao;

    private Long tgChatId;
    private Long linkId;

    @BeforeEach
    void setUp() {
        TestDatabaseContainerDao.cleanDatabase();

        tgChatId = 1L;
        linkId = 1L;

        TestDatabaseContainerDao.getJdbcTemplate()
                .update("INSERT INTO tg_chats (id, created_at) VALUES (?, NOW())", tgChatId);
        TestDatabaseContainerDao.getJdbcTemplate()
                .update("INSERT INTO links (id, url, updated_at) VALUES (?, ?, NOW())", linkId, "https://example.com");
        TestDatabaseContainerDao.getJdbcTemplate()
                .update("INSERT INTO tg_chat_links (tg_chat_id, link_id) VALUES (?, ?)", tgChatId, linkId);
    }

    @AfterEach
    void tearDown() {
        TestDatabaseContainerDao.closeConnections();
    }

    @Test
    @DisplayName("Test: сохранение чата")
    void save() {
        Long chatId = 2L;
        tgChatDao.save(chatId);
        Boolean exists = TestDatabaseContainerDao.getJdbcTemplate()
                .queryForObject("SELECT EXISTS (SELECT 1 FROM tg_chats WHERE id = ?)", Boolean.class, chatId);
        Assertions.assertTrue(exists != null && exists);
    }

    @Test
    @DisplayName("Test: удаление чата")
    void remove() {
        Long chatId = 2L;
        tgChatDao.save(chatId);
        tgChatDao.remove(chatId);
        Boolean exists = TestDatabaseContainerDao.getJdbcTemplate()
                .queryForObject("SELECT EXISTS (SELECT 1 FROM tg_chats WHERE id = ?)", Boolean.class, chatId);
        Assertions.assertFalse(exists != null && exists);
    }

    @Test
    @DisplayName("Test: удаление несуществующего чата")
    void remove_NonExistent() {
        Long chatId = 2L;
        tgChatDao.remove(chatId);

        Boolean exists = TestDatabaseContainerDao.getJdbcTemplate()
                .queryForObject("SELECT EXISTS (SELECT 1 FROM tg_chats WHERE id = ?)", Boolean.class, chatId);
        Assertions.assertFalse(exists != null && exists);
    }
}
