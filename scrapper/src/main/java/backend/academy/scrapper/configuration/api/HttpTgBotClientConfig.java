// package backend.academy.scrapper.configuration.api;
//
// import backend.academy.scrapper.client.HttpUpdateSender;
// import backend.academy.scrapper.client.UpdateSender;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// @Configuration
//// @ConditionalOnProperty(prefix = "app", name = "message-transport", havingValue = "HTTP")
// public class HttpTgBotClientConfig {
//
//    private final String baseUrl;
//    private final WebClientProperties webClientProperties;
//
//    public HttpTgBotClientConfig(
//            @Value("${app.link.telegram-bot-uri}") String baseUrl, WebClientProperties webClientProperties) {
//        this.baseUrl = baseUrl;
//        this.webClientProperties = webClientProperties;
//    }
//
//    @Bean
//    public UpdateSender createHttpTgBotClient() {
//        return new HttpUpdateSender(baseUrl, webClientProperties);
//    }
// }
