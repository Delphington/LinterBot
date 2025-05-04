// package backend.academy.scrapper.configuration.api;
//
// import backend.academy.scrapper.client.HttpUpdateSender;
// import backend.academy.scrapper.client.KafkaUpdateSender;
// import backend.academy.scrapper.client.UpdateSender;
// import backend.academy.scrapper.tracker.update.model.LinkUpdate;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.kafka.core.KafkaTemplate;
//
// @Configuration
// public class UpdateSenderConfig {
//
////    private final String baseUrl;
////    private final WebClientProperties webClientProperties;
////
////    public UpdateSenderConfig(
////        @Value("${app.link.telegram-bot-uri}") String baseUrl, WebClientProperties webClientProperties) {
////        this.baseUrl = baseUrl;
////        this.webClientProperties = webClientProperties;
////    }
////
////    @Bean
////    public UpdateSender createHttpTgBotClient() {
////        return new HttpUpdateSender(baseUrl, webClientProperties);
////    }
//
//
////    @Value("${app.topic}")
////    private String topic;
////
////    @Bean
////    public UpdateSender createHttpTgBotClient(KafkaTemplate<String, LinkUpdate> kafkaTemplate) {
////        return new KafkaUpdateSender(kafkaTemplate, topic);
////    }
// }
