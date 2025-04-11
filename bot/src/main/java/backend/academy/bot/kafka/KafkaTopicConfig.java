package backend.academy.bot.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
public class KafkaTopicConfig {

    @Value("${app.topic}")
    private String topic;

    @Value("${app.topic-dlq}")
    private String topicNameDlq;

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(topic)
            .partitions(1)
            .replicas(1)
            .build();
    }


    @Bean
    public NewTopic topicDlq() {
        return TopicBuilder.name(topicNameDlq)
            .partitions(1)
            .replicas(1)
            .build();
    }

}
