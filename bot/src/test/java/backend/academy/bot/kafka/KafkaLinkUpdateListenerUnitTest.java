package backend.academy.bot.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import backend.academy.bot.api.dto.request.LinkUpdate;
import backend.academy.bot.kafka.client.KafkaLinkUpdateListener;
import backend.academy.bot.notification.NotificationService;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.net.URI;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
public class KafkaLinkUpdateListenerUnitTest {

    @Mock
    private NotificationService notificationService;

    private KafkaLinkUpdateListener kafkaLinkUpdateListener;

    @BeforeEach
    void setUp() {
        kafkaLinkUpdateListener = new KafkaLinkUpdateListener(notificationService);
    }

    @Test
    void testUpdateConsumerCallsNotificationService() {
        // given
        LinkUpdate linkUpdate = new LinkUpdate(42L, URI.create("https://test.com"), "some", Collections.emptyList());

        String topic = "test-link-update-topic";

        // when
        kafkaLinkUpdateListener.updateConsumer(linkUpdate, topic);

        // then
        verify(notificationService).sendMessage(linkUpdate); // Проверка вызова сервиса
    }

    @Test
    void testUpdateConsumerLogsCorrectly() {
        // given
        LinkUpdate linkUpdate = new LinkUpdate(42L, URI.create("https://test.com"), "some", Collections.emptyList());
        String topic = "my-test-topic";

        // Мокаем логгер (чтобы получить сообщения)
        Logger logger = (Logger) LoggerFactory.getLogger(KafkaLinkUpdateListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        // when
        kafkaLinkUpdateListener.updateConsumer(linkUpdate, topic);

        // then
        verify(notificationService).sendMessage(linkUpdate);

        // Проверяем логи
        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Получили информацию из топика: " + topic, "Отправили всю информацию из: " + topic);
    }
}
