package backend.academy.scrapper;

import backend.academy.scrapper.configuration.SchedulerConfig;
import backend.academy.scrapper.configuration.ScrapperConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({ScrapperConfig.class, SchedulerConfig.class})
@EnableScheduling
public class ScrapperApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScrapperApplication.class, args);
    }
}
