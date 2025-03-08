package backend.academy.scrapper.config;


import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.OrmChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "orm")
public class OrmServiceConfig {

    @Bean
    ChatService chatService(ChatRepository chatRepository) {
        return new OrmChatService(chatRepository);
    }
}
