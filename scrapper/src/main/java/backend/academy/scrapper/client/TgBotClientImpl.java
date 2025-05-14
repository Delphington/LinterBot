package backend.academy.scrapper.client;

import backend.academy.scrapper.client.type.HttpUpdateSender;
import backend.academy.scrapper.client.type.KafkaUpdateSender;
import backend.academy.scrapper.tracker.update.model.LinkUpdate;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TgBotClientImpl implements TgBotClient {

    private final HttpUpdateSender httpUpdateSender;
    private final KafkaUpdateSender kafkaUpdateSender;

    private static final String HTTP_TRANSPORT = "http";
    private static final String KAFKA_TRANSPORT = "kafka";

    @Value("${app.message-transport}")
    private String typeUpdateSender;

    @CircuitBreaker(name = "tgBotClient", fallbackMethod = "sendUpdateFallBack")
    @Override
    public void sendUpdate(LinkUpdate linkUpdate) {
        log.info("##### Пошли в http");
        if (HTTP_TRANSPORT.equals(typeUpdateSender)
                || HTTP_TRANSPORT.toUpperCase(Locale.ROOT).equals(typeUpdateSender)) {
            httpUpdateSender.sendUpdate(linkUpdate);
        } else if (KAFKA_TRANSPORT.equals(typeUpdateSender)
                || KAFKA_TRANSPORT.toUpperCase(Locale.ROOT).equals(typeUpdateSender)) {
            log.info("##### Пошли в kafka");
            kafkaUpdateSender.sendUpdate(linkUpdate);
        } else {
            log.error("Unknown update type: {}", linkUpdate);
            throw new RuntimeException("Unknown update type: " + linkUpdate);
        }
    }

    public void sendUpdateFallBack(LinkUpdate linkUpdate, Exception ex) {
        log.error("Ошибка транспорта, меняем его");
        if (HTTP_TRANSPORT.equals(typeUpdateSender)
                || HTTP_TRANSPORT.toUpperCase(Locale.ROOT).equals(typeUpdateSender)) {
            log.info("Значит отправляем в KAFKA");
            kafkaUpdateSender.sendUpdate(linkUpdate);
        } else {
            log.info("Значит отправляем по HTTP");
            httpUpdateSender.sendUpdate(linkUpdate);
        }
    }
}
