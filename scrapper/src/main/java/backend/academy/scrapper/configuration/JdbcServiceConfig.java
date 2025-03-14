package backend.academy.scrapper.configuration;

import backend.academy.scrapper.dao.TgChatLinkDaoImpl;
import backend.academy.scrapper.dao.chat.TgTgChatDaoImpl;
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
    ChatService chatService(TgTgChatDaoImpl chatDao) {
        return new JdbcChatService(chatDao);
    }


    @Bean
    LinkService linkService(TgTgChatDaoImpl chatDao, LinkDaoImpl linkDao,
                            TgChatLinkDaoImpl chatLinkDao, LinkMapper linkMapper) {
        return new JdbcLinkService(chatDao, linkDao, chatLinkDao, linkMapper);
    }
}
