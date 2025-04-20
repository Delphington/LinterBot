//package datebase.dao;
//
//import backend.academy.scrapper.dao.filter.FilterDao;
//import backend.academy.scrapper.dao.filter.FilterDaoImpl;
//import backend.academy.scrapper.entity.Filter;
//import datebase.TestDatabaseContainer;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest(classes = {
//    DataSourceAutoConfiguration.class,
//    JdbcTemplateAutoConfiguration.class,
//    FilterDaoImpl.class
//})
//public class FilterDaoImplTest {
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        TestDatabaseContainer.configureProperties(registry);
//    }
//
//    @Autowired
//    private FilterDao filterDao;
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    private Long tgChatId;
//    private Long linkId;
//
//    @BeforeEach
//    void setUp() {
//        TestDatabaseContainer.cleanDatabase();
//
//        tgChatId = 1L;
//        linkId = 1L;
//
//        jdbcTemplate.update("INSERT INTO tg_chats (id, created_at) VALUES (?, NOW())", tgChatId);
//        jdbcTemplate.update(
//            "INSERT INTO links (id, url, updated_at) VALUES (?, ?, NOW())",
//            linkId, "https://example.com");
//        jdbcTemplate.update(
//            "INSERT INTO tg_chat_links (tg_chat_id, link_id) VALUES (?, ?)",
//            tgChatId, linkId);
//    }
//
//    @DisplayName("Test: поиск фильтров по link_id")
//    @Test
//    void findListFilterByLinkId() {
//        jdbcTemplate.update("INSERT INTO filters (link_id, filter) VALUES (?, ?)", linkId, "java");
//        jdbcTemplate.update("INSERT INTO filters (link_id, filter) VALUES (?, ?)", linkId, "spring");
//
//        List<Filter> filters = filterDao.findListFilterByLinkId(linkId);
//
//        assertEquals(2, filters.size());
//        assertTrue(filters.stream().anyMatch(filter -> filter.filter().equals("java")));
//        assertTrue(filters.stream().anyMatch(filter -> filter.filter().equals("spring")));
//    }
//}
