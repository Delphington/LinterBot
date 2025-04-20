package datebase.service.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.scrapper.dao.accessfilter.AccessFilterDaoImpl;
import backend.academy.scrapper.dto.request.filter.FilterRequest;
import backend.academy.scrapper.dto.response.filter.FilterListResponse;
import backend.academy.scrapper.dto.response.filter.FilterResponse;
import backend.academy.scrapper.service.jdbc.JdbcAccessFilterService;
import datebase.service.TestDatabaseContainerService;
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
            JdbcAccessFilterService.class,
            AccessFilterDaoImpl.class // Реальная реализация DAO
        })
@TestPropertySource(properties = {"app.database-access-type=jdbc", "spring.main.allow-bean-definition-overriding=true"})
class JdbcAccessFilterServiceTest {

    @Autowired
    private JdbcAccessFilterService jdbcAccessFilterService;

    private final Long tgChatId = 1L;
    private final String filterName = "exampleFilter";
    private final FilterRequest filterRequest = new FilterRequest(filterName);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestDatabaseContainerService.configureProperties(registry);
    }

    @BeforeEach
    void setUp() {
        TestDatabaseContainerService.cleanDatabase();
        TestDatabaseContainerService.getJdbcTemplate()
                .update("INSERT INTO tg_chats (id, created_at) VALUES (?, NOW())", tgChatId);
    }

    @AfterEach
    void tearDown() {
        TestDatabaseContainerService.closeConnections();
    }

    @Test
    @DisplayName("Создание и получение фильтра")
    void createAndGetFilter_IntegrationTest() {
        // Создание фильтра
        FilterResponse createdFilter = jdbcAccessFilterService.createFilter(tgChatId, filterRequest);
        assertNotNull(createdFilter);
        assertEquals(filterName, createdFilter.filter());

        // Получение всех фильтров
        FilterListResponse filters = jdbcAccessFilterService.getAllFilter(tgChatId);
        assertEquals(1, filters.filterList().size());
        assertEquals(filterName, filters.filterList().get(0).filter());

        // Удаление фильтра
        FilterResponse deletedFilter = jdbcAccessFilterService.deleteFilter(tgChatId, filterRequest);
        assertNotNull(deletedFilter);
        assertEquals(filterName, deletedFilter.filter());

        // Проверка, что фильтр удален
        assertEquals(
                0,
                TestDatabaseContainerService.getJdbcTemplate()
                        .queryForObject(
                                "SELECT COUNT(*) FROM access_filter WHERE tg_chat_id = ? AND filter = ?",
                                Integer.class,
                                tgChatId,
                                filterName));
    }
}
