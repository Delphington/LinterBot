package backend.academy.scrapper.client;

import backend.academy.scrapper.tracker.update.model.LinkUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaUpdateSender implements UpdateSender {

    private final KafkaTemplate<String, LinkUpdate> kafkaTemplate;

    @Value("${app.topic}")
    private String topic;

    @Override
    public void sendUpdate(LinkUpdate linkUpdate) {
        log.info("Kafka TOPIC: {} ", topic);
        try {
            kafkaTemplate.send(topic, linkUpdate);
            log.info("Сообщение отправлено в kafka");
            // throw new RuntimeException("не получилось");
        } catch (RuntimeException e) {
            log.error("Ошибка при отправки: {}", e.getMessage());
            throw new RuntimeException("Ошибка отправки в kafka");
        }
    }
}
