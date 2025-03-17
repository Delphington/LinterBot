package dao;

import backend.academy.scrapper.dao.tag.TagDao;
import backend.academy.scrapper.entity.Tag;
import base.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public class TagDaoImplTest extends IntegrationTest {

    @Autowired
    private TagDao tagDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long tgChatId;
    private Long linkId;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM tags");
        jdbcTemplate.update("DELETE FROM tg_chat_links");
        jdbcTemplate.update("DELETE FROM links");
        jdbcTemplate.update("DELETE FROM tg_chats");

        tgChatId = 1L;
        linkId = 1L;

        jdbcTemplate.update("INSERT INTO tg_chats (id, created_at) VALUES (?, NOW())", tgChatId);
        jdbcTemplate.update("INSERT INTO links (id, url, updated_at) VALUES (?, ?, NOW())", linkId, "https://example.com");
        jdbcTemplate.update("INSERT INTO tg_chat_links (tg_chat_id, link_id) VALUES (?, ?)", tgChatId, linkId);
    }

    @DisplayName("Test: поиск тегов по link_id")
    @Test
    void findListTagByLinkId() {
        jdbcTemplate.update("INSERT INTO tags (link_id, tag) VALUES (?, ?)", linkId, "java");
        jdbcTemplate.update("INSERT INTO tags (link_id, tag) VALUES (?, ?)", linkId, "spring");
        List<Tag> tags = tagDao.findListTagByLinkId(linkId);
        Assertions.assertEquals(2, tags.size());
        Assertions.assertTrue(tags.stream().anyMatch(tag -> tag.tag().equals("java")));
        Assertions.assertTrue(tags.stream().anyMatch(tag -> tag.tag().equals("spring")));
    }

    @DisplayName("Test: удаление тега")
    @Transactional
    @Test
    void removeTag() {
        String tag = "docker";
        jdbcTemplate.update("INSERT INTO tags (link_id, tag) VALUES (?, ?)", linkId, tag);
        tagDao.removeTag(linkId, tag);
        List<Tag> tags = tagDao.findListTagByLinkId(linkId);
        Assertions.assertTrue(tags.isEmpty());
    }

    @DisplayName("Test: удаление несуществующего тега")
    @Transactional
    @Test
    void removeNonExistentTag() {
        tagDao.removeTag(linkId, "nonexistent");
        List<Tag> tags = tagDao.findListTagByLinkId(linkId);
        Assertions.assertTrue(tags.isEmpty());
    }
}
