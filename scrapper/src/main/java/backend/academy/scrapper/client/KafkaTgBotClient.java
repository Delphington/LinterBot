package backend.academy.scrapper.client;

import backend.academy.scrapper.tracker.update.model.LinkUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public class KafkaTgBotClient implements TgBotClient {

    private final KafkaTemplate<String, LinkUpdate> kafkaTemplate;

    private final String topic;

    @Override
    public void addUpdate(LinkUpdate linkUpdate) {
        log.info("Kafka TOPIC: {} ", topic);
        try {
            kafkaTemplate.send(topic, linkUpdate);
            log.info("Сообщение отправлено в kafka");
        } catch (RuntimeException e) {
            log.error("Ошибка при отправки: {}", e.getMessage());
        }
    }
}
