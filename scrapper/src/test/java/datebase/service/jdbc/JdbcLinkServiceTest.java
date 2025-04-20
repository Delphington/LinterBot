package datebase.service.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.scrapper.dao.TgChatLinkDaoImpl;
import backend.academy.scrapper.dao.chat.TgChatDaoImpl;
import backend.academy.scrapper.dao.link.LinkDaoImpl;
import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.exception.chat.ChatNotExistException;
import backend.academy.scrapper.exception.link.LinkAlreadyExistException;
import backend.academy.scrapper.exception.link.LinkNotFoundException;
import backend.academy.scrapper.mapper.LinkMapper;
import backend.academy.scrapper.service.jdbc.JdbcLinkService;
import datebase.service.TestDatabaseContainerService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
        classes = {
            DataSourceAutoConfiguration.class,
            JdbcTemplateAutoConfiguration.class,
            JdbcLinkService.class,
            TgChatDaoImpl.class,
            LinkDaoImpl.class,
            TgChatLinkDaoImpl.class,
            LinkMapper.class
        })
@TestPropertySource(properties = {"app.database-access-type=jdbc", "spring.main.allow-bean-definition-overriding=true"})
class JdbcLinkServiceTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestDatabaseContainerService.configureProperties(registry);
    }

    @Autowired
    private JdbcLinkService jdbcLinkService;

    private final Long tgChatId = 1L;
    private final URI uri = URI.create("https://example.com");
    private final AddLinkRequest addLinkRequest =
            new AddLinkRequest(uri, Collections.emptyList(), Collections.emptyList());

    @BeforeEach
    void setUp() {
        TestDatabaseContainerService.cleanDatabase();

        // Добавление тестового чата
        TestDatabaseContainerService.getJdbcTemplate()
                .update("INSERT INTO tg_chats (id, created_at) VALUES (?, ?)", tgChatId, OffsetDateTime.now());
    }

    @AfterEach
    void tearDown() {
        TestDatabaseContainerService.closeConnections();
    }

    @Test
    @DisplayName("Получение списка ссылок для чата - должен вернуть пустой список для нового чата")
    void findAllLinksByChatId_ShouldReturnEmptyListForNewChat() {
        ListLinksResponse response = jdbcLinkService.findAllLinksByChatId(tgChatId);

        assertNotNull(response);
        assertEquals(0, response.size());
    }

    @Test
    @DisplayName("Добавление ссылки - должен успешно добавить ссылку и вернуть ответ")
    void addLink_ShouldAddLinkAndReturnLinkResponse() {
        LinkResponse response = jdbcLinkService.addLink(tgChatId, addLinkRequest);

        assertNotNull(response);
        assertEquals(uri, response.url());

        // Проверка что ссылка действительно добавлена в БД
        Integer count = TestDatabaseContainerService.getJdbcTemplate()
                .queryForObject("SELECT COUNT(*) FROM links WHERE url = ?", Integer.class, uri.toString());
        assertEquals(1, count);
    }

    @Test
    @DisplayName("Добавление ссылки - должен выбросить исключение при повторном добавлении")
    void addLink_ShouldThrowLinkAlreadyExistException_WhenLinkAlreadyExists() {
        jdbcLinkService.addLink(tgChatId, addLinkRequest);

        assertThrows(LinkAlreadyExistException.class, () -> jdbcLinkService.addLink(tgChatId, addLinkRequest));
    }

    @Test
    @DisplayName("Удаление ссылки - должен успешно удалить ссылку")
    void deleteLink_ShouldDeleteLinkAndReturnLinkResponse() {
        LinkResponse addedLink = jdbcLinkService.addLink(tgChatId, addLinkRequest);

        LinkResponse response = jdbcLinkService.deleteLink(tgChatId, uri);

        assertNotNull(response);
        assertEquals(addedLink.id(), response.id());

        // Проверка что ссылка удалена из БД
        Integer count = TestDatabaseContainerService.getJdbcTemplate()
                .queryForObject("SELECT COUNT(*) FROM links WHERE id = ?", Integer.class, addedLink.id());
        assertEquals(0, count);
    }

    @Test
    @DisplayName("Удаление ссылки - должен выбросить исключение при несуществующем чате")
    void deleteLink_ShouldThrowChatNotExistException_WhenChatDoesNotExist() {
        assertThrows(ChatNotExistException.class, () -> jdbcLinkService.deleteLink(999L, uri));
    }

    @Test
    @DisplayName("Удаление ссылки - должен выбросить исключение при несуществующей ссылке")
    void deleteLink_ShouldThrowLinkNotFoundException_WhenLinkDoesNotExist() {
        assertThrows(LinkNotFoundException.class, () -> jdbcLinkService.deleteLink(tgChatId, uri));
    }

    @Test
    @DisplayName("Поиск ссылки по ID - должен вернуть ссылку при её наличии")
    void findById_ShouldReturnLink_WhenLinkExists() {
        LinkResponse addedLink = jdbcLinkService.addLink(tgChatId, addLinkRequest);

        Optional<Link> result = jdbcLinkService.findById(addedLink.id());

        assertTrue(result.isPresent());
        assertEquals(addedLink.id(), result.get().id());
    }

    @Test
    @DisplayName("Поиск ссылки по ID - должен вернуть пустой Optional при отсутствии ссылки")
    void findById_ShouldReturnEmptyOptional_WhenLinkDoesNotExist() {
        Optional<Link> result = jdbcLinkService.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Обновление ссылки - должен успешно обновить данные ссылки")
    void update_ShouldUpdateLink() {
        LinkResponse addedLink = jdbcLinkService.addLink(tgChatId, addLinkRequest);

        Link updatedLink = new Link()
                .id(addedLink.id())
                .url(uri.toString())
                .description("updated description")
                .updatedAt(OffsetDateTime.now());

        jdbcLinkService.update(updatedLink);

        // Проверка обновления в БД
        String description = TestDatabaseContainerService.getJdbcTemplate()
                .queryForObject("SELECT description FROM links WHERE id = ?", String.class, addedLink.id());
        assertEquals("updated description", description);
    }
}
