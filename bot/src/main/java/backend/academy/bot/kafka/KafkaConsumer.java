package backend.academy.bot.kafka;

import backend.academy.bot.api.dto.request.LinkUpdate;
import backend.academy.bot.executor.RequestExecutor;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {

    private final RequestExecutor execute;

    @KafkaListener(topics = "${app.topic}",
        groupId = "${spring.kafka.consumer.group-id}",
        properties = {"spring.json.value.default.type=backend.academy.bot.api.dto.request.LinkUpdate"})
    public void updateConsumer(LinkUpdate linkUpdate, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Получили информацию из топика: {}", topic);
        for (Long chatId : linkUpdate.tgChatIds()) {
            SendMessage sendMessage = new SendMessage(
                chatId, String.format("Обновление по ссылке: %s%n %s", linkUpdate.url(), linkUpdate.description()));
            execute.execute(sendMessage);
        }
        log.info("Отправили всю информацию из: {}", topic);
    }

}
