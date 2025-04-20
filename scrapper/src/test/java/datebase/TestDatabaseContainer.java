package datebase;

import java.io.File;
import java.nio.file.Path;
import java.sql.DriverManager;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class TestDatabaseContainer {
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
                    DockerImageName.parse("postgres:15"))
            .withDatabaseName("scrapper_db")
            .withUsername("postgres")
            .withPassword("postgres");

    static {
        POSTGRES.start();
        runMigrations();
    }

    private static void runMigrations() {
        try (var connection =
                DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {

            Path changeLogPath = new File(".")
                    .toPath()
                    .toAbsolutePath()
                    .getParent()
                    .getParent()
                    .resolve("migrations");

            var db = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            new Liquibase("master.xml", new DirectoryResourceAccessor(changeLogPath), db)
                    .update(new Contexts(), new LabelExpression());
        } catch (Exception e) {
            throw new RuntimeException("Failed to run migrations", e);
        }
    }

    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    private static volatile JdbcTemplate jdbcTemplate; // Добавляем volatile

    public static synchronized void cleanDatabase() {
        if (jdbcTemplate == null) {
            initJdbcTemplate();
        }

        // Очищаем таблицы с учетом зависимостей
        try {
            jdbcTemplate.update("DELETE FROM tg_chat_links");
            jdbcTemplate.update("DELETE FROM access_filter");
            jdbcTemplate.update("DELETE FROM filters");
            jdbcTemplate.update("DELETE FROM tags");
            jdbcTemplate.update("DELETE FROM links");
            jdbcTemplate.update("DELETE FROM tg_chats");
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean database", e);
        }
    }

    private static synchronized void initJdbcTemplate() {
        if (jdbcTemplate == null) {
            DataSource dataSource = DataSourceBuilder.create()
                    .url(POSTGRES.getJdbcUrl())
                    .username(POSTGRES.getUsername())
                    .password(POSTGRES.getPassword())
                    .build();
            jdbcTemplate = new JdbcTemplate(dataSource);
        }
    }
}
