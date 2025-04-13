package backend.academy.bot.kafka.client;

import backend.academy.bot.api.dto.request.LinkUpdate;
import backend.academy.bot.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaLinkUpdateListener {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${app.topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            properties = {"spring.json.value.default.type=backend.academy.bot.api.dto.request.LinkUpdate"})
    public void updateConsumer(LinkUpdate linkUpdate, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Получили информацию из топика: {}", topic);
        notificationService.sendMessage(linkUpdate);
        log.info("Отправили всю информацию из: {}", topic);
    }
}
