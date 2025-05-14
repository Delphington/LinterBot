package backend.academy.scrapper.configuration.db;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("backend.academy.scrapper.repository")
@EntityScan("backend.academy.scrapper.entity")
public class JpaConfig {}
