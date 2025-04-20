package datebase.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import backend.academy.scrapper.dao.accessfilter.AccessFilterDaoImpl;
import backend.academy.scrapper.dto.request.filter.FilterRequest;
import backend.academy.scrapper.dto.response.filter.FilterListResponse;
import backend.academy.scrapper.dto.response.filter.FilterResponse;
import backend.academy.scrapper.exception.filter.AccessFilterNotExistException;
import datebase.TestDatabaseContainerDao;
import lombok.extern.slf4j.Slf4j;
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

@SpringBootTest(
        classes = {DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, AccessFilterDaoImpl.class})
@Slf4j
public class AccessFilterDaoImplTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestDatabaseContainerDao.configureProperties(registry);
    }

    @Autowired
    private AccessFilterDaoImpl accessFilterDao;

    Long tgChatId;
    Long linkId;

    @BeforeEach
    void clearDatabase() {
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

    @Test
    @DisplayName("Создание фильтра - успешный сценарий")
    void createFilter_shouldCreateAndReturnFilter() {
        // Given
        Long tgChatId = 1L;
        FilterRequest request = new FilterRequest("test-filter");

        // When
        FilterResponse response = accessFilterDao.createFilter(tgChatId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.filter()).isEqualTo("test-filter");
        assertThat(response.id()).isNotNull();

        // Проверяем, что фильтр действительно сохранен в БД
        Integer count = TestDatabaseContainerDao.getJdbcTemplate()
                .queryForObject(
                        "SELECT COUNT(*) FROM access_filter WHERE id = ? AND filter = ?",
                        Integer.class,
                        response.id(),
                        "test-filter");
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("Проверка существования фильтра - фильтр существует")
    void filterExists_shouldReturnTrueWhenFilterExists() {
        Long tgChatId = 1L;
        String filter = "existing-filter";
        TestDatabaseContainerDao.getJdbcTemplate()
                .update("INSERT INTO access_filter (tg_chat_id, filter) VALUES (?, ?)", tgChatId, filter);
        boolean exists = accessFilterDao.filterExists(filter);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Проверка существования фильтра - фильтр не существует")
    void filterExists_shouldReturnFalseWhenFilterNotExists() {
        boolean exists = accessFilterDao.filterExists("non-existent-filter");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Получение всех фильтров для chatId")
    void getAllFilter_shouldReturnAllFiltersForChatId() {
        // Given
        Long tgChatId = 1L;
        Long otherChatId = 2L;
        TestDatabaseContainerDao.getJdbcTemplate()
                .update("INSERT INTO tg_chats (id, created_at) VALUES (?, NOW())", otherChatId);

        TestDatabaseContainerDao.getJdbcTemplate()
                .update(
                        "INSERT INTO access_filter (tg_chat_id, filter) VALUES (?, ?), (?, ?), (?, ?)",
                        tgChatId,
                        "filter1",
                        tgChatId,
                        "filter2",
                        otherChatId,
                        "other-filter");

        // When
        FilterListResponse response = accessFilterDao.getAllFilter(tgChatId);

        // Then
        assertThat(response.filterList()).hasSize(2);
        assertThat(response.filterList().stream().map(FilterResponse::filter))
                .containsExactlyInAnyOrder("filter1", "filter2");
    }

    @Test
    @DisplayName("Получение всех фильтров - пустой результат")
    void getAllFilter_shouldReturnEmptyListWhenNoFilters() {
        // When
        FilterListResponse response = accessFilterDao.getAllFilter(1L);

        // Then
        assertThat(response.filterList()).isEmpty();
    }

    @Test
    @DisplayName("Удаление фильтра - успешный сценарий")
    void deleteFilter_shouldDeleteAndReturnDeletedFilter() {
        // Given
        Long tgChatId = 1L;
        String filter = "to-delete";
        TestDatabaseContainerDao.getJdbcTemplate()
                .update("INSERT INTO access_filter (tg_chat_id, filter) VALUES (?, ?)", tgChatId, filter);

        // When
        FilterResponse response = accessFilterDao.deleteFilter(tgChatId, new FilterRequest(filter));

        // Then
        assertThat(response.filter()).isEqualTo(filter);

        // Проверяем, что фильтр удален
        Integer count = TestDatabaseContainerDao.getJdbcTemplate()
                .queryForObject("SELECT COUNT(*) FROM access_filter WHERE filter = ?", Integer.class, filter);
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("Удаление фильтра - фильтр не существует")
    void deleteFilter_shouldThrowWhenFilterNotExists() {
        // Given
        Long tgChatId = 1L;
        FilterRequest request = new FilterRequest("non-existent");

        // When & Then
        assertThatThrownBy(() -> accessFilterDao.deleteFilter(tgChatId, request))
                .isInstanceOf(AccessFilterNotExistException.class)
                .hasMessageContaining("Filter not found for deletion");
    }

    @Test
    @DisplayName("Удаление фильтра - проверка транзакционности")
    void deleteFilter_shouldBeTransactional() {
        // Given
        Long tgChatId = 1L;
        String filter = "transaction-test";
        TestDatabaseContainerDao.getJdbcTemplate()
                .update("INSERT INTO access_filter (tg_chat_id, filter) VALUES (?, ?)", tgChatId, filter);

        // When & Then
        assertThatThrownBy(() -> accessFilterDao.deleteFilter(tgChatId, new FilterRequest("wrong-filter")))
                .isInstanceOf(AccessFilterNotExistException.class);

        // Проверяем, что оригинальный фильтр не удален
        Integer count = TestDatabaseContainerDao.getJdbcTemplate()
                .queryForObject("SELECT COUNT(*) FROM access_filter WHERE filter = ?", Integer.class, filter);
        assertThat(count).isEqualTo(1);
    }
}
