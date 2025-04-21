package backend.academy.bot.integration.kafka;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import backend.academy.bot.api.dto.request.LinkUpdate;
import backend.academy.bot.integration.KafkaTestContainer;
import backend.academy.bot.kafka.client.KafkaLinkUpdateListener;
import backend.academy.bot.notification.MessageUpdateSender;
import backend.academy.bot.notification.NotificationProperties;
import backend.academy.bot.notification.NotificationService;
import backend.academy.bot.redis.RedisMessageService;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;

@Slf4j
@DirtiesContext
public class KafkaLinkUpdateListenerTest {

    private KafkaTemplate<String, LinkUpdate> kafkaTemplate;

    private KafkaLinkUpdateListener kafkaLinkUpdateListener;

    @Mock
    private NotificationProperties properties;

    @Mock
    private MessageUpdateSender messageUpdateSender;

    @Mock
    private RedisMessageService redisMessageService;

    @Mock
    private NotificationService notificationService;

    private static final String TOPIC = "updated-topic";

    @BeforeEach
    void setUp() {
        // Инициализация моков
        MockitoAnnotations.openMocks(this);

        // Настройка моков для messageUpdateSender
        doNothing().when(messageUpdateSender).sendMessage(Mockito.any(LinkUpdate.class));
        doNothing().when(notificationService).sendMessage(Mockito.any(LinkUpdate.class));

        kafkaTemplate = KafkaTestContainer.createKafkaTemplate();
        kafkaLinkUpdateListener = new KafkaLinkUpdateListener(notificationService);
    }

    @Test
    @DisplayName("Тестирование KafkaUpdatesListener#listenUpdate с корректными данными")
    public void listenUpdateShouldCatchUpdate() {
        var linkUpdate = new LinkUpdate(1L, URI.create("http://test.com"), "test", List.of(1L));

        // Отправляем сообщение в Kafka
        kafkaTemplate.send(TOPIC, linkUpdate);

        // Симулируем вызов метода updateConsumer, как если бы он был вызван KafkaListener
        kafkaLinkUpdateListener.updateConsumer(linkUpdate, TOPIC);

        // Проверяем, что метод sendMessage был вызван
        verify(notificationService, times(1)).sendMessage(linkUpdate);
    }
}
