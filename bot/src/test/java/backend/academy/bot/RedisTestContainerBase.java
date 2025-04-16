//package backend.academy.bot;
//
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
////import org.testcontainers.utility.DockerImageName;
//
//@Testcontainers
//public class RedisTestContainerBase {
//
//    @Container
//    public static final GenericContainer<?> REDIS_CONTAINER =
//        new GenericContainer<>(DockerImageName.parse("redis:latest"))
//            .withExposedPorts(6379);
//
//    @DynamicPropertySource
//    static void redisProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
//        registry.add("spring.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
//        registry.add("spring.redis.password", () -> "");
//    }
//
//}
