package backend.academy.bot.integration.kafka;

import backend.academy.bot.api.dto.kafka.BadLink;
import backend.academy.bot.integration.KafkaTestContainer;
import backend.academy.bot.kafka.client.KafkaInvalidLinkProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Slf4j
@DirtiesContext
public class KafkaInvalidLinkProducerTest {

    private KafkaTemplate<String, BadLink> kafkaTemplate;

    @InjectMocks
    private KafkaInvalidLinkProducer producer;

    private static final String TOPIC = "dead-letter-queue";

    @BeforeEach
    void setUp() {
        kafkaTemplate = KafkaTestContainer.createKafkaTemplateBad();
        producer = new KafkaInvalidLinkProducer(kafkaTemplate, TOPIC);

        // Устанавливаем значение для final-поля через рефлексию
        ReflectionTestUtils.setField(producer, "topic", TOPIC);
    }



    @Test
    @DisplayName("Тестирование отправки невалидной ссылки в DLQ")
    public void shouldSendInvalidLinkToDlq() {
        // Arrange
        BadLink badLink = new BadLink(404L, "http://invalid.url");

        // Создаем consumer для проверки сообщений в DLQ
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            KafkaTestContainer.kafka.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-dlq-consumer");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            JsonDeserializer.class.getName());
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        KafkaConsumer<String, BadLink> dlqConsumer = new KafkaConsumer<>(consumerProps);
        dlqConsumer.subscribe(Collections.singletonList(TOPIC));

        // Act
        producer.sendInvalidLink(badLink);

        // Assert
        await()
            .pollInterval(Duration.ofMillis(100))
            .atMost(Duration.ofSeconds(10))
            .untilAsserted(() -> {
                ConsumerRecords<String, BadLink> records = dlqConsumer.poll(Duration.ofMillis(100));
                assertThat(records.count()).isEqualTo(1);
                assertThat(records.iterator().next().value()).isEqualTo(badLink);
            });

        dlqConsumer.close();
    }
}
