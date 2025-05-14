package backend.academy.bot.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import backend.academy.bot.api.dto.kafka.BadLink;
import backend.academy.bot.kafka.client.KafkaInvalidLinkProducer;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
public class KafkaInvalidLinkProducerTest {

    @Mock
    private KafkaTemplate<String, BadLink> kafkaTemplate;

    private KafkaInvalidLinkProducer kafkaInvalidLinkProducer;

    private final String topic = "test-dlq-topic";

    private Logger logger;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setup() {
        kafkaInvalidLinkProducer = new KafkaInvalidLinkProducer(kafkaTemplate, topic);

        // Настраиваем логгер и ListAppender ЗАРАНЕЕ
        logger = (Logger) LoggerFactory.getLogger(KafkaInvalidLinkProducer.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        // Отключаем аппендер после каждого теста, чтобы не мешал другим
        logger.detachAppender(listAppender);
    }

    @Test
    void testSendInvalidLink_SuccessfulSend() {
        // given
        BadLink badLink = new BadLink(1L, "https://bad-link.com");

        // when
        kafkaInvalidLinkProducer.sendInvalidLink(badLink);

        // then
        verify(kafkaTemplate).send(eq(topic), eq(badLink));

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("kafka topic: " + topic, "Сообщение отправлено в kafka");
    }

    @Test
    void testSendInvalidLink_FailureSend_LogsError() {
        // given
        BadLink badLink = new BadLink(1L, "https://bad-link.com");
        doThrow(new RuntimeException("Kafka send failed")).when(kafkaTemplate).send(any(), any());

        // when
        kafkaInvalidLinkProducer.sendInvalidLink(badLink);

        // then
        verify(kafkaTemplate).send(eq(topic), eq(badLink));

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("kafka topic: " + topic, "Ошибка при отправки: Kafka send failed");
    }

    @Test
    void testSendInvalidLink_ExceptionDoesNotPropagate() {
        // given
        BadLink badLink = new BadLink(1L, "https://bad-link.com");
        doThrow(new RuntimeException("Kafka error")).when(kafkaTemplate).send(any(), any());

        // when & then
        assertThatNoException().isThrownBy(() -> kafkaInvalidLinkProducer.sendInvalidLink(badLink));

        assertThat(listAppender.list).extracting(ILoggingEvent::getLevel).contains(Level.ERROR);
    }
}
