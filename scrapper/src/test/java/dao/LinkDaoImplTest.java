// package dao;
//
// import backend.academy.scrapper.dao.link.LinkDao;
// import backend.academy.scrapper.dto.request.AddLinkRequest;
// import backend.academy.scrapper.entity.Link;
// import base.IntegrationTest;
// import java.net.URI;
// import java.time.OffsetDateTime;
// import java.time.ZoneOffset;
// import java.util.List;
// import java.util.Optional;
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.transaction.annotation.Transactional;
//
// public class LinkDaoImplTest extends IntegrationTest {
//
//    @Autowired
//    private LinkDao linkDao;
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    private Long tgChatId;
//    private Long linkId;
//
//    @BeforeEach
//    void setUp() {
//        // Очистка таблиц перед каждым тестом (в правильном порядке)
//        jdbcTemplate.update("DELETE FROM tags");
//        jdbcTemplate.update("DELETE FROM filters");
//        jdbcTemplate.update("DELETE FROM tg_chat_links");
//        jdbcTemplate.update("DELETE FROM links");
//        jdbcTemplate.update("DELETE FROM tg_chats");
//
//        // Подготовка данных
//        tgChatId = 1L;
//
//        // Вставляем тестовый чат
//        jdbcTemplate.update("INSERT INTO tg_chats (id, created_at) VALUES (?, NOW())", tgChatId);
//
//        // Вставляем тестовую ссылку (без указания chatId, чтобы база данных сгенерировала его автоматически)
//        jdbcTemplate.update(
//                "INSERT INTO links (url, description, updated_at) VALUES (?, ?, ?)",
//                "https://example.com",
//                "Example description",
//                OffsetDateTime.now(ZoneOffset.UTC));
//
//        // Получаем ID вставленной ссылки
//        linkId = jdbcTemplate.queryForObject("SELECT id FROM links WHERE url = ?", Long.class, "https://example.com");
//
//        // Связываем чат и ссылку
//        jdbcTemplate.update("INSERT INTO tg_chat_links (tg_chat_id, link_id) VALUES (?, ?)", tgChatId, linkId);
//    }
//
//    @DisplayName("Test: получение ссылки по ID")
//    @Transactional
//    @Test
//    void findLinkByLinkId() {
//        // Подготовка данных: добавляем теги и фильтры для ссылки
//        jdbcTemplate.update("INSERT INTO tags (link_id, tag) VALUES (?, ?)", linkId, "java");
//        jdbcTemplate.update("INSERT INTO filters (link_id, filter) VALUES (?, ?)", linkId, "spring");
//
//        // Выполнение метода
//        Optional<Link> linkOptional = linkDao.findLinkByLinkId(linkId);
//
//        // Проверка результата
//        Assertions.assertTrue(linkOptional.isPresent());
//        Link link = linkOptional.get();
//        Assertions.assertEquals(linkId, link.id());
//        Assertions.assertEquals("https://example.com", link.url());
//        Assertions.assertEquals("Example description", link.description());
//        Assertions.assertEquals(1, link.tags().size());
//        Assertions.assertEquals(1, link.filters().size());
//    }
//
//    @DisplayName("Test: добавление ссылки")
//    @Transactional
//    @Test
//    void addLink() {
//        // Подготовка данных
//        AddLinkRequest request = new AddLinkRequest(
//                URI.create("https://new-example.com"), List.of("java", "spring"), List.of("filter1", "filter2"));
//
//        // Выполнение метода
//        Long newLinkId = linkDao.addLink(request);
//
//        // Проверка результата
//        Assertions.assertNotNull(newLinkId);
//
//        // Проверка, что ссылка добавлена
//        Optional<Link> linkOptional = linkDao.findLinkByLinkId(newLinkId);
//        Assertions.assertTrue(linkOptional.isPresent());
//        Link link = linkOptional.get();
//        Assertions.assertEquals("https://new-example.com", link.url());
//        Assertions.assertEquals(2, link.tags().size());
//        Assertions.assertEquals(2, link.filters().size());
//    }
//
//    @DisplayName("Test: получение всех ссылок")
//    @Test
//    void getAllLinks() {
//        // Подготовка данных: добавляем несколько ссылок
//        jdbcTemplate.update(
//                "INSERT INTO links (url, description, updated_at) VALUES (?, ?, ?)",
//                "https://example1.com",
//                "Example 1",
//                OffsetDateTime.now(ZoneOffset.UTC));
//        jdbcTemplate.update(
//                "INSERT INTO links (url, description, updated_at) VALUES (?, ?, ?)",
//                "https://example2.com",
//                "Example 2",
//                OffsetDateTime.now(ZoneOffset.UTC));
//
//        // Выполнение метода
//        List<Link> links = linkDao.getAllLinks(0, 10);
//
//        // Проверка результата
//        Assertions.assertEquals(3, links.size());
//    }
// }
