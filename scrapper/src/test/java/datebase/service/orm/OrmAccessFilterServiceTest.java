package datebase.service.orm;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.scrapper.configuration.db.JpaConfig;
import backend.academy.scrapper.dto.request.filter.FilterRequest;
import backend.academy.scrapper.dto.response.filter.FilterListResponse;
import backend.academy.scrapper.dto.response.filter.FilterResponse;
import backend.academy.scrapper.exception.chat.ChatNotExistException;
import backend.academy.scrapper.exception.filter.AccessFilterAlreadyExistException;
import backend.academy.scrapper.exception.filter.AccessFilterNotExistException;
import backend.academy.scrapper.mapper.FilterMapper;
import backend.academy.scrapper.service.orm.OrmAccessFilterService;
import backend.academy.scrapper.service.orm.OrmChatService;
import datebase.TestDatabaseContainerDao;
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
            OrmAccessFilterService.class,
            OrmChatService.class,
            JpaConfig.class,
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            FilterMapper.class
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
public class OrmAccessFilterServiceTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestDatabaseContainerDao.configureProperties(registry);
    }

    private final Long tgChatId = 1L;
    private final String testFilter = "exampleFilter";

    @Autowired
    private OrmChatService ormChatService;

    @Autowired
    private OrmAccessFilterService ormAccessFilterService;

    @BeforeEach
    void setUp() {
        TestDatabaseContainerDao.cleanDatabase();
        ormChatService.registerChat(tgChatId);
    }

    @Test
    @DisplayName("Создание фильтра → успешно создает новый фильтр")
    @Transactional
    void createFilter_ShouldCreateNewFilter() {
        FilterRequest request = new FilterRequest(testFilter);
        FilterResponse response = ormAccessFilterService.createFilter(tgChatId, request);

        assertAll(() -> assertNotNull(response.id()), () -> assertEquals(testFilter, response.filter()));
    }

    @Test
    @DisplayName("Создание фильтра → выбрасывает исключение при дубликате фильтра")
    @Transactional
    void createFilter_ShouldThrowException_WhenFilterExists() {
        FilterRequest request = new FilterRequest(testFilter);
        ormAccessFilterService.createFilter(tgChatId, request);

        assertThrows(
                AccessFilterAlreadyExistException.class, () -> ormAccessFilterService.createFilter(tgChatId, request));
    }

    @Test
    @DisplayName("Создание фильтра → выбрасывает исключение при отсутствии чата")
    @Transactional
    void createFilter_ShouldThrowException_WhenChatNotExists() {
        Long nonExistentChatId = 999L;
        FilterRequest request = new FilterRequest(testFilter);

        assertThrows(
                ChatNotExistException.class, () -> ormAccessFilterService.createFilter(nonExistentChatId, request));
    }

    @Test
    @DisplayName("Получение всех фильтров → возвращает пустой список при отсутствии фильтров")
    @Transactional
    void getAllFilter_ShouldReturnEmptyList_WhenNoFilters() {
        FilterListResponse response = ormAccessFilterService.getAllFilter(tgChatId);
        assertTrue(response.filterList().isEmpty());
    }

    @Test
    @DisplayName("Удаление фильтра → выбрасывает исключение при отсутствии фильтра")
    @Transactional
    void deleteFilter_ShouldThrowException_WhenFilterNotExists() {
        assertThrows(
                AccessFilterNotExistException.class,
                () -> ormAccessFilterService.deleteFilter(tgChatId, new FilterRequest(testFilter)));
    }

    @Test
    @DisplayName("Удаление фильтра → выбрасывает исключение при отсутствии чата")
    @Transactional
    void deleteFilter_ShouldThrowException_WhenChatNotExists() {
        Long nonExistentChatId = 999L;
        assertThrows(
                ChatNotExistException.class,
                () -> ormAccessFilterService.deleteFilter(nonExistentChatId, new FilterRequest(testFilter)));
    }
}
