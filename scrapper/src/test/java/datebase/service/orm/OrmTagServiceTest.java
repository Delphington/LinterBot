package datebase.service.orm;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.scrapper.configuration.db.JpaConfig;
import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.dto.request.tag.TagRemoveRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.dto.response.TagListResponse;
import backend.academy.scrapper.entity.TgChat;
import backend.academy.scrapper.exception.link.LinkNotFoundException;
import backend.academy.scrapper.exception.tag.TagNotExistException;
import backend.academy.scrapper.mapper.LinkMapper;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.orm.OrmChatService;
import backend.academy.scrapper.service.orm.OrmLinkService;
import backend.academy.scrapper.service.orm.OrmTagService;
import datebase.TestDatabaseContainer;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = {
            OrmTagService.class,
            OrmLinkService.class,
            OrmChatService.class,
            JpaConfig.class,
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            LinkMapper.class,
        })
@TestPropertySource(
        properties = {
            "app.database-access-type=orm",
            "spring.jpa.hibernate.ddl-auto=validate",
            "spring.jpa.show-sql=true",
            "spring.test.database.replace=none",
            "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect"
        })
@ActiveProfiles("orm")
class OrmTagServiceTest {

    @Autowired
    private ChatService chatService;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestDatabaseContainer.configureProperties(registry);
    }

    @Autowired
    private OrmTagService ormTagService;

    @BeforeEach
    void setUp() {
        TestDatabaseContainer.cleanDatabase();
        ormChatService.registerChat(tgChatId);

        // Проверка, что чат создан с инициализированной коллекцией
        TgChat chat = chatService.findChatById(tgChatId).orElseThrow();
        assertNotNull(chat.id());
    }

    @Autowired
    private OrmLinkService ormLinkService;

    @Autowired
    private OrmChatService ormChatService;

    private final Long tgChatId = 1L;
    private final URI uri = URI.create("https://example.com");
    private final String tagName = "exampleTag";

    @Test
    @DisplayName("При удалении тега из несуществующей ссылки → выбрасывается LinkNotFoundException")
    void removeTagFromNonExistentLink_ThrowsLinkNotFoundException() {
        TagRemoveRequest request = new TagRemoveRequest(tagName, uri);
        assertThrows(LinkNotFoundException.class, () -> ormTagService.removeTagFromLink(tgChatId, request));
    }

    @Test
    @DisplayName("При удалении несуществующего тега → выбрасывается TagNotExistException")
    @Transactional
    void removeNonExistentTag_ThrowsTagNotExistException() {
        ormLinkService.addLink(tgChatId, new AddLinkRequest(uri, List.of("otherTag"), List.of()));
        assertThrows(
                TagNotExistException.class,
                () -> ormTagService.removeTagFromLink(tgChatId, new TagRemoveRequest(tagName, uri)));
    }

    @Test
    @DisplayName("При удалении существующего тега → тег успешно удаляется из ссылки")
    @Transactional
    void removeExistingTag_RemovesTagSuccessfully() {
        ormLinkService.addLink(tgChatId, new AddLinkRequest(uri, List.of(tagName, "persistentTag"), List.of()));

        LinkResponse response = ormTagService.removeTagFromLink(tgChatId, new TagRemoveRequest(tagName, uri));

        assertAll(
                () -> assertFalse(response.tags().contains(tagName)),
                () -> assertTrue(response.tags().contains("persistentTag")));
    }

    @Test
    @DisplayName("При запросе ссылок по тегу → возвращаются только ссылки с этим тегом")
    @Transactional
    void getLinksByTag_ReturnsOnlyMatchingLinks() {
        URI uri1 = URI.create("https://example.com/1");
        URI uri2 = URI.create("https://example.com/2");
        String targetTag = "targetTag";

        ormLinkService.addLink(tgChatId, new AddLinkRequest(uri1, List.of(targetTag, "commonTag"), List.of()));
        ormLinkService.addLink(tgChatId, new AddLinkRequest(uri2, List.of("commonTag"), List.of()));

        ListLinksResponse result = ormTagService.getListLinkByTag(tgChatId, targetTag);

        assertAll(
                () -> assertEquals(1, result.links().size()),
                () -> assertTrue(result.links().get(0).tags().contains(targetTag)));
    }

    @Test
    @DisplayName("При запросе всех тегов → возвращаются уникальные теги без дубликатов")
    @Transactional
    void getAllTags_ReturnsUniqueTags() {
        ormLinkService.addLink(tgChatId, new AddLinkRequest(uri, List.of("tag1", "tag1", "tag2"), List.of()));

        TagListResponse result = ormTagService.getAllListLinks(tgChatId);

        assertAll(
                () -> assertEquals(2, result.tags().size()),
                () -> assertTrue(result.tags().containsAll(List.of("tag1", "tag2"))));
    }

    @Test
    @DisplayName("При запросе тегов для чата без ссылок → возвращается пустой список")
    void getTagsForChatWithoutLinks_ReturnsEmptyList() {
        TagListResponse result = ormTagService.getAllListLinks(tgChatId);
        assertTrue(result.tags().isEmpty());
    }

    @Test
    @DisplayName("При удалении тега → другие теги той же ссылки остаются неизменными")
    @Transactional
    void removeTag_DoesNotAffectOtherTags() {
        ormLinkService.addLink(tgChatId, new AddLinkRequest(uri, List.of("tag1", "tag2", "tag3"), List.of()));

        LinkResponse response = ormTagService.removeTagFromLink(tgChatId, new TagRemoveRequest("tag2", uri));

        assertAll(
                () -> assertFalse(response.tags().contains("tag2")),
                () -> assertTrue(response.tags().contains("tag1")),
                () -> assertTrue(response.tags().contains("tag3")));
    }
}
