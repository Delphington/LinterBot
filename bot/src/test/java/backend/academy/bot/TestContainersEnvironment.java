//package backend.academy.bot.command;
//
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.kafka.KafkaContainer;
//import org.testcontainers.utility.DockerImageName;
//
//public class TestContainersEnvironment {
//    @Container
//    public static final KafkaContainer KAFKA =
//        new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
//
//    @Container
//    public static final GenericContainer<?> REDIS =
//        new GenericContainer<>(DockerImageName.parse("redis:latest"))
//            .withExposedPorts(6379);
//
//    static {
//        // Можно добавить логирование или дополнительную настройку
//        KAFKA.start();
//        REDIS.start();
//
//        System.setProperty("spring.kafka.bootstrap-servers", KAFKA.getBootstrapServers());
//        System.setProperty("spring.data.redis.url",
//            String.format("redis://%s:%d", REDIS.getHost(), REDIS.getFirstMappedPort()));
//    }
//}
