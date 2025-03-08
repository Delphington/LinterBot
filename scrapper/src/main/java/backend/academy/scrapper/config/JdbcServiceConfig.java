package backend.academy.scrapper.config;

import backend.academy.scrapper.dao.ChatDao;
import backend.academy.scrapper.dao.ChatDaoImpl;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.JdbcChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcServiceConfig {
    @Bean
    ChatService chatService(ChatDaoImpl chatDao) {
        return new JdbcChatService(chatDao);
    }
}
