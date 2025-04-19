// package backend.academy.bot.integration.kafka;
//
// import backend.academy.bot.api.dto.request.LinkUpdate;
// import backend.academy.bot.executor.RequestExecutor;
// import backend.academy.bot.integration.RedisTestContainer;
// import backend.academy.bot.kafka.client.KafkaLinkUpdateListener;
// import backend.academy.bot.notification.MessageUpdateSender;
// import backend.academy.bot.notification.NotificationService;
// import backend.academy.bot.redis.RedisMessageService;
// import com.pengrad.telegrambot.request.SendMessage;
// import lombok.SneakyThrows;
// import org.apache.kafka.clients.consumer.ConsumerConfig;
// import org.apache.kafka.clients.consumer.ConsumerRecords;
// import org.apache.kafka.clients.consumer.KafkaConsumer;
// import org.apache.kafka.common.serialization.StringDeserializer;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mock;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
// import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.context.TestConfiguration;
// import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
// import org.springframework.context.annotation.Bean;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.kafka.support.serializer.JsonDeserializer;
// import org.springframework.test.annotation.DirtiesContext;
// import org.springframework.test.context.DynamicPropertyRegistry;
// import org.springframework.test.context.DynamicPropertySource;
// import org.testcontainers.junit.jupiter.Container;
// import org.testcontainers.junit.jupiter.Testcontainers;
// import org.testcontainers.kafka.KafkaContainer;
// import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.kafka.core.ConsumerFactory;
// import org.testcontainers.utility.DockerImageName;
// import java.net.URI;
// import java.time.Duration;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.concurrent.TimeUnit;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.timeout;
// import static org.mockito.Mockito.verify;
// import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
//
// @SpringBootTest
// @Testcontainers
// @DirtiesContext(classMode = AFTER_CLASS)
// public class KafkaLinkUpdateListenerTest {
//
//    private static final DockerImageName KAFKA_IMAGE = DockerImageName
//        .parse("confluentinc/cp-kafka:7.5.0")
//        .asCompatibleSubstituteFor("confluentinc/cp-kafka"); // было "apache/kafka"
//
//    private static final KafkaContainer kafka = new KafkaContainer(KAFKA_IMAGE);
//
//    @Autowired
//    private KafkaTemplate<String, LinkUpdate> kafkaTemplate;
//
//    @Autowired
//    private KafkaConsumer<String, LinkUpdate> kafkaConsumer;
//
//    @Value("${app.topic}")
//    private String topic;
//
//    @BeforeAll
//    static void startContainer() {
//        kafka.start();
//    }
//
//    @DynamicPropertySource
//    static void kafkaProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
//    }
//
//    @Test
//    @SneakyThrows
//    void shouldCorrectlyReadMessageFromKafkaTopic() {
//        // given
//        LinkUpdate expectedLinkUpdate = new LinkUpdate(
//            1L,
//            URI.create("http://example.com"),
//            "Test description",
//            List.of(123L, 456L)
//        );
//
//        // when
//        kafkaTemplate.send(topic, expectedLinkUpdate).get(10, TimeUnit.SECONDS);
//
//        // then
//        ConsumerRecords<String, LinkUpdate> records = kafkaConsumer.poll(Duration.ofSeconds(10));
//        assertThat(records.count()).isEqualTo(1);
//
//        LinkUpdate actualLinkUpdate = records.iterator().next().value();
//        assertThat(actualLinkUpdate)
//            .usingRecursiveComparison()
//            .isEqualTo(expectedLinkUpdate);
//    }
// }
//
//
//
// @TestConfiguration
// class KafkaTestConfig {
//    @Value("${app.topic}") // была незакрытая кавычка
//    private String topic;
//
//    @Bean
//    public KafkaConsumer<String, LinkUpdate> kafkaConsumer(KafkaProperties kafkaProperties) {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//
//        // Добавим настройки для JsonDeserializer
//        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
//        props.put(JsonDeserializer.TYPE_MAPPINGS, "linkUpdate:backend.academy.bot.api.dto.request.LinkUpdate");
//
//        KafkaConsumer<String, LinkUpdate> consumer = new KafkaConsumer<>(props);
//        consumer.subscribe(Collections.singletonList(topic));
//        return consumer;
//    }
// }
