package datebase.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.scrapper.dao.filter.FilterDao;
import backend.academy.scrapper.dao.filter.FilterDaoImpl;
import backend.academy.scrapper.entity.Filter;
import datebase.TestDatabaseContainerDao;
import java.util.List;
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

@SpringBootTest(classes = {DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, FilterDaoImpl.class})
public class FilterDaoImplTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestDatabaseContainerDao.configureProperties(registry);
    }

    @Autowired
    private FilterDao filterDao;

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

    @DisplayName("Test: поиск фильтров по link_id")
    @Test
    void findListFilterByLinkId() {
        TestDatabaseContainerDao.getJdbcTemplate()
                .update("INSERT INTO filters (link_id, filter) VALUES (?, ?)", linkId, "java");
        TestDatabaseContainerDao.getJdbcTemplate()
                .update("INSERT INTO filters (link_id, filter) VALUES (?, ?)", linkId, "spring");

        List<Filter> filters = filterDao.findListFilterByLinkId(linkId);

        assertEquals(2, filters.size());
        assertTrue(filters.stream().anyMatch(filter -> filter.filter().equals("java")));
        assertTrue(filters.stream().anyMatch(filter -> filter.filter().equals("spring")));
    }
}
