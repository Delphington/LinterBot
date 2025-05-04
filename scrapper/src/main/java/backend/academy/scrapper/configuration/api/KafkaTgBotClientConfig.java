package backend.academy.scrapper.configuration.api;

// @Configuration
//// @ConditionalOnProperty(prefix = "app", name = "message-transport", havingValue = "kafka")
// public class KafkaTgBotClientConfig {
//
//    @Value("${app.topic}")
//    private String topic;
//
//    @Bean
//    public UpdateSender createHttpTgBotClient(KafkaTemplate<String, LinkUpdate> kafkaTemplate) {
//        return new KafkaUpdateSender(kafkaTemplate, topic);
//    }
// }
