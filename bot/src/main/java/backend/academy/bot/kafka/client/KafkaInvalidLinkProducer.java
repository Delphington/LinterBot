package backend.academy.bot.kafka.client;

import backend.academy.bot.api.dto.kafka.BadLink;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaInvalidLinkProducer {

    private final KafkaTemplate<String, BadLink> kafkaTemplate;

    @Value("${app.topic-dlq}")
    private final String topic;

    public void sendInvalidLink(BadLink badLink) {
        log.info("Мы в kafka topic: " + topic);
        try {
            kafkaTemplate.send(topic, badLink);
            log.info("Сообщение отправлено в kafka");
        } catch (RuntimeException e) {
            log.error("Ошибка при отправки: {}", e.getMessage());
        }
    }
}
