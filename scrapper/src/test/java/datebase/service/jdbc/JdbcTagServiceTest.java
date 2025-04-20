package datebase.service.jdbc;

import backend.academy.scrapper.dao.TgChatLinkDaoImpl;
import backend.academy.scrapper.dao.filter.FilterDaoImpl;
import backend.academy.scrapper.dao.link.LinkDaoImpl;
import backend.academy.scrapper.dao.tag.TagDaoImpl;
import backend.academy.scrapper.dto.request.tag.TagRemoveRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.dto.response.TagListResponse;
import backend.academy.scrapper.exception.link.LinkNotFoundException;
import backend.academy.scrapper.exception.tag.TagNotExistException;
import backend.academy.scrapper.mapper.LinkMapper;
import backend.academy.scrapper.service.jdbc.JdbcTagService;
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
import org.springframework.test.context.TestPropertySource;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
    DataSourceAutoConfiguration.class,
    JdbcTemplateAutoConfiguration.class,
    JdbcTagService.class,
    FilterDaoImpl.class,
    TagDaoImpl.class,
    LinkDaoImpl.class,
    TgChatLinkDaoImpl.class,
    LinkMapper.class
})
@TestPropertySource(properties = {
    "app.database-access-type=jdbc",
    "spring.main.allow-bean-definition-overriding=true"
})
class JdbcTagServiceTest {

    @Autowired
    private JdbcTagService jdbcTagService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long tgChatId;
    private Long linkId;
    private final URI uri = URI.create("https://example.com");
    private final String tagName = "exampleTag";

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestDatabaseContainer.configureProperties(registry);
    }

    @BeforeEach
    void setUp() {
        TestDatabaseContainer.cleanDatabase();

        tgChatId = 1L;
        linkId = 1L;

        // Настройка тестовых данных
        jdbcTemplate.update(
            "INSERT INTO tg_chats (id, created_at) VALUES (?, ?)",
            tgChatId, OffsetDateTime.now(ZoneId.systemDefault()));

        jdbcTemplate.update(
            "INSERT INTO links (id, url, updated_at, description) VALUES (?, ?, ?, ?)",
            linkId, uri.toString(), OffsetDateTime.now(ZoneId.systemDefault()), "Test description");

        jdbcTemplate.update(
            "INSERT INTO tg_chat_links (tg_chat_id, link_id) VALUES (?, ?)",
            tgChatId, linkId);
    }

    private void insertTestTag() {
        jdbcTemplate.update(
            "INSERT INTO tags (link_id, tag) VALUES (?, ?)",
            linkId, tagName);
    }

    @Test
    @DisplayName("Получение списка ссылок по тегу - должен вернуть непустой ответ с корректными данными")
    void getListLinkByTag_ShouldReturnListLinksResponse() {
        insertTestTag();
        ListLinksResponse response = jdbcTagService.getListLinkByTag(tgChatId, tagName);
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(uri.toString(), response.links().get(0).url().toString());
    }

    @Test
    @DisplayName("Получение всех тегов для чата - должен вернуть список содержащий тест-тег")
    void getAllListLinks_ShouldReturnTagListResponse() {
        insertTestTag();
        TagListResponse response = jdbcTagService.getAllListLinks(tgChatId);
        assertNotNull(response);
        assertTrue(response.tags().contains(tagName));
    }

    @Test
    @DisplayName("Удаление тега из ссылки - должен успешно удалить тег и вернуть ответ")
    void removeTagFromLink_ShouldRemoveTagAndReturnLinkResponse() {
        insertTestTag();
        TagRemoveRequest request = new TagRemoveRequest(tagName, uri);
        LinkResponse response = jdbcTagService.removeTagFromLink(tgChatId, request);
        assertNotNull(response);
        assertEquals(0, jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM tags WHERE link_id = ? AND tag = ?",
            Integer.class, linkId, tagName));
    }

    @Test
    @DisplayName("Удаление тега из несуществующей ссылки - должен выбросить LinkNotFoundException")
    void removeTagFromLink_ShouldThrowLinkNotFoundException_WhenLinkDoesNotExist() {
        TagRemoveRequest request = new TagRemoveRequest(tagName, URI.create("https://nonexistent.com"));
        assertThrows(LinkNotFoundException.class, () ->
            jdbcTagService.removeTagFromLink(tgChatId, request));
    }

    @Test
    @DisplayName("Удаление несуществующего тега - должен выбросить TagNotExistException")
    void removeTagFromLink_ShouldThrowTagNotExistException_WhenTagDoesNotExist() {
        TagRemoveRequest request = new TagRemoveRequest("nonexistent-tag", uri);
        assertThrows(TagNotExistException.class, () ->
            jdbcTagService.removeTagFromLink(tgChatId, request));
    }
}

