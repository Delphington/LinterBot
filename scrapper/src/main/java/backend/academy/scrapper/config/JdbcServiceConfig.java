package backend.academy.scrapper.config;

import backend.academy.scrapper.dao.ChatLinkDaoImpl;
import backend.academy.scrapper.dao.chat.ChatDaoImpl;
import backend.academy.scrapper.dao.link.LinkDaoImpl;
import backend.academy.scrapper.mapper.LinkMapper;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.service.jdbc.JdbcChatService;
import backend.academy.scrapper.service.jdbc.JdbcLinkService;
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


    @Bean
    LinkService linkService(ChatDaoImpl chatDao, LinkDaoImpl linkDao,
                            ChatLinkDaoImpl chatLinkDao, LinkMapper linkMapper) {
        return new JdbcLinkService(chatDao, linkDao, chatLinkDao, linkMapper);
    }
}
