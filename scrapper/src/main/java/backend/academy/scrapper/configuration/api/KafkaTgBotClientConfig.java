package backend.academy.scrapper.configuration.api;

import backend.academy.scrapper.client.HttpTgBotClient;
import backend.academy.scrapper.client.KafkaTgBotClient;
import backend.academy.scrapper.client.TgBotClient;
import backend.academy.scrapper.tracker.update.model.LinkUpdate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "message-transport", havingValue = "kafka")
public class KafkaTgBotClientConfig {

    @Value("${app.topic}")
    private String topic;

    @Bean
    public TgBotClient createHttpTgBotClient(KafkaTemplate<String, LinkUpdate> kafkaTemplate) {
        return new KafkaTgBotClient(kafkaTemplate, topic);
    }
}
