package base;

import backend.academy.scrapper.ScrapperApplication;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.DriverManager;
import java.sql.SQLException;
import backend.academy.scrapper.dao.accessfilter.AccessFilterDaoImpl;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.DirectoryResourceAccessor;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class IntegrationTest {

    public static PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("scrapper_db")
                .withUsername("postgres")
                .withPassword("postgres");
        POSTGRES.start();

        try {
            runMigrations(POSTGRES);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runMigrations(JdbcDatabaseContainer<?> c) throws FileNotFoundException {
        try (var connection = DriverManager.getConnection(c.getJdbcUrl(), c.getUsername(), c.getPassword())) {
            var changeLogPath = new File(".")
                    .toPath()
                    .toAbsolutePath()
                    .getParent()
                    .getParent()
                    .resolve("migrations");
            var db = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            var liquibase = new Liquibase("master.xml", new DirectoryResourceAccessor(changeLogPath), db);
            liquibase.update(new Contexts(), new LabelExpression());
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
