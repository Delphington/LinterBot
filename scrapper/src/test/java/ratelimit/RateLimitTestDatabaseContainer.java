package ratelimit;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class RateLimitTestDatabaseContainer {
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
                    DockerImageName.parse("postgres:15"))
            .withDatabaseName("scrapper_db")
            .withUsername("postgres")
            .withPassword("postgres")
            .withReuse(true);

    static {
        POSTGRES.start();
        // Увеличиваем лимит соединений для тестовой БД
        try (Connection conn = DriverManager.getConnection(
                        POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
                Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER SYSTEM SET max_connections = 200");
            stmt.execute("SELECT pg_reload_conf()");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to increase max_connections", e);
        }
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
}
