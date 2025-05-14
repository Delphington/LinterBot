package datebase.service.orm;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.scrapper.configuration.db.JpaConfig;
import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.exception.chat.ChatNotExistException;
import backend.academy.scrapper.exception.link.LinkAlreadyExistException;
import backend.academy.scrapper.exception.link.LinkNotFoundException;
import backend.academy.scrapper.mapper.LinkMapper;
import backend.academy.scrapper.service.orm.OrmChatService;
import backend.academy.scrapper.service.orm.OrmLinkService;
import datebase.TestDatabaseContainerDao;
import java.net.URI;
import java.util.List;
import java.util.Optional;
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
            OrmLinkService.class,
            LinkMapper.class,
            OrmChatService.class,
            JpaConfig.class,
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class
        })
@TestPropertySource(
        properties = {
            "spring.jpa.hibernate.ddl-auto=validate",
            "spring.jpa.show-sql=true",
            "spring.test.database.replace=none",
            "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect"
        })
@ActiveProfiles("orm")
class OrmLinkServiceTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestDatabaseContainerDao.configureProperties(registry);
    }

    @Autowired
    private OrmLinkService ormLinkService;

    @Autowired
    private OrmChatService ormChatService;

    private final Long tgChatId = 1L;
    private final URI uri = URI.create("https://example.com");
    private final AddLinkRequest addLinkRequest = new AddLinkRequest(uri, List.of("tag1", "tag2"), List.of("filter1"));

    @BeforeEach
    void setUp() {
        TestDatabaseContainerDao.cleanDatabase();
        ormChatService.registerChat(tgChatId);
    }

    @Test
    @DisplayName("Добавление ссылки → успешно создает новую ссылку")
    @Transactional
    void addLink_ShouldCreateNewLink() {
        LinkResponse response = ormLinkService.addLink(tgChatId, addLinkRequest);
        assertAll(
                () -> assertNotNull(response.id()),
                () -> assertEquals(uri, response.url()),
                () -> assertEquals(2, response.tags().size()),
                () -> assertEquals(1, response.filters().size()));
    }

    @Test
    @DisplayName("Добавление ссылки → выбрасывает исключение при дубликате ссылки")
    @Transactional
    void addLink_ShouldThrowException_WhenLinkExists() {
        ormLinkService.addLink(tgChatId, addLinkRequest);

        assertThrows(LinkAlreadyExistException.class, () -> ormLinkService.addLink(tgChatId, addLinkRequest));
    }

    @Test
    @DisplayName("Добавление ссылки → выбрасывает исключение при отсутствии чата")
    @Transactional
    void addLink_ShouldThrowException_WhenChatNotExists() {
        Long nonExistentChatId = 999L;

        assertThrows(ChatNotExistException.class, () -> ormLinkService.addLink(nonExistentChatId, addLinkRequest));
    }

    @Test
    @DisplayName("Удаление ссылки → успешно удаляет существующую ссылку")
    @Transactional
    void deleteLink_ShouldRemoveExistingLink() {
        LinkResponse addedLink = ormLinkService.addLink(tgChatId, addLinkRequest);
        LinkResponse deletedLink = ormLinkService.deleteLink(tgChatId, uri);

        assertEquals(addedLink.id(), deletedLink.id());
    }

    @Test
    @DisplayName("Удаление ссылки → выбрасывает исключение при отсутствии ссылки")
    @Transactional
    void deleteLink_ShouldThrowException_WhenLinkNotExists() {
        assertThrows(LinkNotFoundException.class, () -> ormLinkService.deleteLink(tgChatId, uri));
    }

    @Test
    @DisplayName("Поиск ссылки по ID → возвращает ссылку при ее наличии")
    @Transactional
    void findById_ShouldReturnLink_WhenExists() {
        LinkResponse addedLink = ormLinkService.addLink(tgChatId, addLinkRequest);
        Optional<Link> foundLink = ormLinkService.findById(addedLink.id());

        assertTrue(foundLink.isPresent());
        assertEquals(addedLink.id(), foundLink.get().id());
    }

    @Test
    @DisplayName("Поиск ссылки по ID → возвращает пустой Optional при отсутствии ссылки")
    @Transactional
    void findById_ShouldReturnEmpty_WhenNotExists() {
        Optional<Link> foundLink = ormLinkService.findById(999L);
        assertFalse(foundLink.isPresent());
    }

    @Test
    @DisplayName("Обновление ссылки → успешно обновляет данные")
    @Transactional
    void update_ShouldUpdateLink() {
        LinkResponse addedLink = ormLinkService.addLink(tgChatId, addLinkRequest);
        Link linkToUpdate = new Link();
        linkToUpdate.id(addedLink.id());
        linkToUpdate.url(uri.toString());
        linkToUpdate.description("Updated description");

        ormLinkService.update(linkToUpdate);

        Optional<Link> updatedLink = ormLinkService.findById(addedLink.id());
        assertTrue(updatedLink.isPresent());
        assertEquals("Updated description", updatedLink.get().description());
    }

    @Test
    @DisplayName("Получение списка ссылок → возвращает ссылки для указанного чата")
    @Transactional
    void findAllLinksByChatId_ShouldReturnLinksForChat() {
        ormLinkService.addLink(tgChatId, addLinkRequest);
        ListLinksResponse response = ormLinkService.findAllLinksByChatId(tgChatId);

        assertEquals(1, response.links().size());
        assertEquals(uri, response.links().get(0).url());
    }

    @Test
    @DisplayName("Получение списка ссылок → возвращает пустой список для чата без ссылок")
    @Transactional
    void findAllLinksByChatId_ShouldReturnEmptyList_WhenNoLinks() {
        ListLinksResponse response = ormLinkService.findAllLinksByChatId(tgChatId);
        assertTrue(response.links().isEmpty());
    }
}
