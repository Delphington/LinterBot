// package backend.academy.bot.integration.kafka;
//
// import backend.academy.bot.api.dto.request.LinkUpdate;
// import backend.academy.bot.kafka.client.KafkaLinkUpdateListener;
// import backend.academy.bot.notification.MessageUpdateSender;
// import backend.academy.bot.notification.NotificationProperties;
// import backend.academy.bot.notification.NotificationService;
// import backend.academy.bot.redis.RedisMessageService;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.test.annotation.DirtiesContext;
// import org.springframework.test.context.DynamicPropertyRegistry;
// import org.springframework.test.context.DynamicPropertySource;
// import org.springframework.test.util.ReflectionTestUtils;
// import org.testcontainers.junit.jupiter.Container;
// import org.testcontainers.junit.jupiter.Testcontainers;
// import org.testcontainers.kafka.KafkaContainer;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.testcontainers.utility.DockerImageName;
// import java.net.URI;
// import java.time.Duration;
// import java.util.List;
// import java.util.concurrent.TimeUnit;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.timeout;
// import static org.mockito.Mockito.verify;
// import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
// import static org.testcontainers.shaded.org.awaitility.Awaitility.await;
// @SpringBootTest(classes = KafkaAutoConfiguration.class)
// @Testcontainers
// public class KafkaLinkUpdateListenerTest {
//
//    @Container
//    public static final KafkaContainer KAFKA = new KafkaContainer(
//        DockerImageName.parse("confluentinc/cp-kafka:7.3.0")
//            .asCompatibleSubstituteFor("apache/kafka")
//    )
//        .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
//        .withStartupTimeout(Duration.ofMinutes(2));
//
//    @DynamicPropertySource
//    static void kafkaProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
//    }
//
//    @Test
//    void shouldSendKafkaMessage() {
//        assertTrue(KAFKA.isRunning());
//    }
// }
////
////    @DynamicPropertySource
////    static void kafkaProperties(DynamicPropertyRegistry registry) {
////        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
////        registry.add("spring.kafka.consumer.bootstrap-servers", KAFKA::getBootstrapServers);
////        registry.add("spring.kafka.producer.bootstrap-servers", KAFKA::getBootstrapServers);
////    }
//
////    @Autowired
////    private KafkaTemplate<String, LinkUpdate> kafkaTemplate;
////
////    @Autowired
////    private KafkaLinkUpdateListener kafkaListener;
////
////    private NotificationService notificationService;
////
////    @DynamicPropertySource
////    static void kafkaProperties(DynamicPropertyRegistry registry) {
////        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
////        registry.add("app.topic", () -> "updates");
////    }
////
////    @BeforeEach
////    void setUp() {
////        notificationService = mock(NotificationService.class);
////        ReflectionTestUtils.setField(kafkaListener, "notificationService", notificationService);
////    }
////
////    @Test
////    void shouldProcessKafkaMessage() throws Exception {
////        LinkUpdate linkUpdate = new LinkUpdate(
////            1L, URI.create("http://test.com"), "test", List.of(1L)
////        );
////
////        kafkaTemplate.send("updates", linkUpdate).get(10, TimeUnit.SECONDS);
////        verify(notificationService, timeout(5000)).sendMessage(linkUpdate);
////    }
//
