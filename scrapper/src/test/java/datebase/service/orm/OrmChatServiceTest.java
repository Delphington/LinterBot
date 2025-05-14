package datebase.service.orm;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.scrapper.configuration.db.JpaConfig;
import backend.academy.scrapper.entity.TgChat;
import backend.academy.scrapper.exception.chat.ChatAlreadyExistsException;
import backend.academy.scrapper.service.orm.OrmChatService;
import datebase.TestDatabaseContainerDao;
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
            OrmChatService.class,
            JpaConfig.class,
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class
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
class OrmChatServiceTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestDatabaseContainerDao.configureProperties(registry);
    }

    @Autowired
    private OrmChatService ormChatService;

    private final Long tgChatId = 1L;

    @BeforeEach
    void setUp() {
        TestDatabaseContainerDao.cleanDatabase();
    }

    @Test
    @DisplayName("Регистрация чата - должен успешно сохранить новый чат")
    @Transactional
    void registerChat_ShouldRegisterChat() {
        ormChatService.registerChat(tgChatId);
        Optional<TgChat> foundChat = ormChatService.findChatById(tgChatId);
        assertTrue(foundChat.isPresent());
        assertEquals(tgChatId, foundChat.get().id());
    }

    @Test
    @DisplayName("Регистрация чата - должен выбросить исключение при существующем чате")
    @Transactional
    void registerChat_ShouldThrowChatAlreadyExistsException_WhenChatAlreadyExists() {
        ormChatService.registerChat(tgChatId);
        assertThrows(ChatAlreadyExistsException.class, () -> ormChatService.registerChat(tgChatId));
    }

    @Test
    @DisplayName("Поиск чата по ID - должен вернуть чат при его наличии")
    @Transactional
    void findChatById_ShouldReturnChat_WhenChatExists() {
        ormChatService.registerChat(tgChatId);
        Optional<TgChat> foundChat = ormChatService.findChatById(tgChatId);
        assertTrue(foundChat.isPresent());
        assertEquals(tgChatId, foundChat.get().id());
    }

    @Test
    @DisplayName("Поиск чата по ID - должен вернуть пустой Optional при отсутствии чата")
    @Transactional
    void findChatById_ShouldReturnEmptyOptional_WhenChatDoesNotExist() {
        // Act
        Optional<TgChat> foundChat = ormChatService.findChatById(tgChatId);

        // Assert
        assertFalse(foundChat.isPresent());
    }
}
