package datebase.dao;

import backend.academy.scrapper.dao.tag.TagDaoImpl;
import backend.academy.scrapper.entity.Tag;
import datebase.TestDatabaseContainerDao;
import java.util.List;
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

@SpringBootTest(classes = {DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, TagDaoImpl.class})
public class TagDaoImplTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestDatabaseContainerDao.configureProperties(registry);
    }

    @Autowired
    private TagDaoImpl tagDao;

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
    @DisplayName("Test: поиск тегов по link_id")
    void findListTagByLinkId() {
        TestDatabaseContainerDao.getJdbcTemplate()
                .update("INSERT INTO tags (link_id, tag) VALUES (?, ?)", linkId, "java");
        TestDatabaseContainerDao.getJdbcTemplate()
                .update("INSERT INTO tags (link_id, tag) VALUES (?, ?)", linkId, "spring");
        List<Tag> tags = tagDao.findListTagByLinkId(linkId);
        Assertions.assertEquals(2, tags.size());
        Assertions.assertTrue(tags.stream().anyMatch(tag -> tag.tag().equals("java")));
        Assertions.assertTrue(tags.stream().anyMatch(tag -> tag.tag().equals("spring")));
    }

    @Test
    @DisplayName("Test: поиск тегов по link_id, если тестов нет")
    void findListTagByLinkIdWithoutTags() {
        List<Tag> tags = tagDao.findListTagByLinkId(linkId);
        Assertions.assertNotNull(tags);
    }

    @Test
    @DisplayName("Test: удаление тега")
    void removeTag() {
        String tag = "docker";
        TestDatabaseContainerDao.getJdbcTemplate().update("INSERT INTO tags (link_id, tag) VALUES (?, ?)", linkId, tag);
        tagDao.removeTag(linkId, tag);
        List<Tag> tags = tagDao.findListTagByLinkId(linkId);
        Assertions.assertTrue(tags.isEmpty());
    }

    @DisplayName("Test: удаление несуществующего тега")
    @Test
    void removeNonExistentTag() {
        tagDao.removeTag(linkId, "nonexistent");
        List<Tag> tags = tagDao.findListTagByLinkId(linkId);
        Assertions.assertTrue(tags.isEmpty());
    }
}
