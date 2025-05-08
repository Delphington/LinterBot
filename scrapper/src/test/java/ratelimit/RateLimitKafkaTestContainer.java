package ratelimit;

import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class RateLimitKafkaTestContainer {

    @Container
    public static org.testcontainers.containers.KafkaContainer kafka = new org.testcontainers.containers.KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    static {
        kafka.start();
    }


    @DynamicPropertySource
    public static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.embedded.kafka.brokers", kafka::getBootstrapServers);
    }
}
