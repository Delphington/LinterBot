// package dao;
//
// import backend.academy.scrapper.dao.filter.FilterDao;
// import backend.academy.scrapper.entity.Filter;
// import base.IntegrationTest;
// import java.util.List;
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.jdbc.core.JdbcTemplate;
//
// public class FilterDaoImplTest extends IntegrationTest {
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
//        jdbcTemplate.update("DELETE FROM filters");
//        jdbcTemplate.update("DELETE FROM tg_chat_links");
//        jdbcTemplate.update("DELETE FROM links");
//        jdbcTemplate.update("DELETE FROM tg_chats");
//
//        tgChatId = 1L;
//        linkId = 1L;
//
//        jdbcTemplate.update("INSERT INTO tg_chats (id, created_at) VALUES (?, NOW())", tgChatId);
//
//        jdbcTemplate.update(
//                "INSERT INTO links (id, url, updated_at) VALUES (?, ?, NOW())", linkId, "https://example.com");
//
//        jdbcTemplate.update("INSERT INTO tg_chat_links (tg_chat_id, link_id) VALUES (?, ?)", tgChatId, linkId);
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
//        Assertions.assertEquals(2, filters.size());
//        Assertions.assertTrue(
//                filters.stream().anyMatch(filter -> filter.filter().equals("java")));
//        Assertions.assertTrue(
//                filters.stream().anyMatch(filter -> filter.filter().equals("spring")));
//    }
// }
