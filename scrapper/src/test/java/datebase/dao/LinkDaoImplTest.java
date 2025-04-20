package datebase.dao;

import backend.academy.scrapper.dao.link.LinkDaoImpl;
import backend.academy.scrapper.entity.Link;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import backend.academy.scrapper.exception.link.LinkNotFoundException;
import datebase.TestDatabaseContainer;
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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
    DataSourceAutoConfiguration.class,
    JdbcTemplateAutoConfiguration.class,
    LinkDaoImpl.class
})
public class LinkDaoImplTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestDatabaseContainer.configureProperties(registry);
    }

    @Autowired
    private LinkDaoImpl linkDao;

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
    @DisplayName("Получение ссылки по ID - успешный сценарий")
    void findLinkByLinkId_Success() {
        jdbcTemplate.update("INSERT INTO tags (link_id, tag) VALUES (?, ?)", linkId, "java");
        jdbcTemplate.update("INSERT INTO filters (link_id, filter) VALUES (?, ?)", linkId, "spring");
        Optional<Link> result = linkDao.findLinkByLinkId(linkId);

        assertTrue(result.isPresent());
        Link link = result.get();
        assertEquals(linkId, link.id());
        assertEquals("https://example.com", link.url());
        assertEquals(1, link.tags().size());
        assertEquals(1, link.filters().size());
    }

    @Test
    @DisplayName("Получение ссылки по ID - ссылка не найдена")
    void findLinkByLinkId_NotFound() {
        Optional<Link> result = linkDao.findLinkByLinkId(999L);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Добавление ссылки без тегов и фильтров")
    void addLink_WithoutTagsAndFilters() {
        Optional<Link> link = linkDao.findLinkByLinkId(linkId);
        assertTrue(link.isPresent());
        assertEquals("https://example.com", link.get().url());
        assertTrue(link.get().tags().isEmpty());
        assertTrue(link.get().filters().isEmpty());
    }


    @Test
    @DisplayName("Удаление существующей ссылки")
    void remove_ExistingLink() {
        assertDoesNotThrow(() -> linkDao.remove(linkId));
        assertEquals(0, jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM links WHERE id = ?", Integer.class, linkId));
    }

    @Test
    @DisplayName("Удаление несуществующей ссылки")
    void remove_NonExistingLink() {
        assertDoesNotThrow(() -> linkDao.remove(999L));
    }

    @Test
    @DisplayName("Получение списка ссылок по IDs")
    void getListLinksByListLinkId_Success() {
        // Добавляем вторую ссылку
        Long secondLinkId = 2L;
        jdbcTemplate.update(
            "INSERT INTO links (id, url, updated_at) VALUES (?, ?, ?)",
            secondLinkId, "https://example2.com", OffsetDateTime.now(ZoneOffset.UTC));

        List<Link> result = linkDao.getListLinksByListLinkId(List.of(linkId, secondLinkId));

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(l -> l.id().equals(linkId)));
        assertTrue(result.stream().anyMatch(l -> l.id().equals(secondLinkId)));
    }

    @Test
    @DisplayName("Получение списка ссылок по IDs - одна ссылка не найдена")
    void getListLinksByListLinkId_OneNotFound() {
        assertThrows(LinkNotFoundException.class,
            () -> linkDao.getListLinksByListLinkId(List.of(linkId, 999L)));
    }

    @Test
    @DisplayName("Обновление существующей ссылки")
    void update_ExistingLink() {
        Link link = new Link()
            .id(linkId)
            .url("https://updated.com")
            .description("Updated description")
            .updatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        assertDoesNotThrow(() -> linkDao.update(link));

        Optional<Link> updatedLink = linkDao.findLinkByLinkId(linkId);
        assertTrue(updatedLink.isPresent());
        assertEquals("Updated description", updatedLink.get().description());
    }

    @Test
    @DisplayName("Поиск ссылок по chatId с фильтрацией")
    void findAllLinksByChatIdWithFilter() {
        // Настройка тестовых данных
        jdbcTemplate.update("INSERT INTO filters (link_id, filter) VALUES (?, ?)", linkId, "java");
        jdbcTemplate.update("INSERT INTO access_filter (tg_chat_id, filter) VALUES (?, ?)", tgChatId, "spring");

        List<Link> result = linkDao.findAllLinksByChatIdWithFilter(0, 10);

        assertEquals(1, result.size());
        assertEquals(linkId, result.get(0).id());
    }

    @Test
    @DisplayName("Поиск ссылок по chatId с фильтрацией - нет совпадений по фильтрам")
    void findAllLinksByChatIdWithFilter_NoMatches() {
        jdbcTemplate.update("INSERT INTO filters (link_id, filter) VALUES (?, ?)", linkId, "java");
        jdbcTemplate.update("INSERT INTO access_filter (tg_chat_id, filter) VALUES (?, ?)", tgChatId, "java");

        List<Link> result = linkDao.findAllLinksByChatIdWithFilter(0, 10);

        assertTrue(result.isEmpty());
    }
}
