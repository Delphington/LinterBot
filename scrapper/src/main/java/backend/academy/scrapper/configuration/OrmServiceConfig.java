package backend.academy.scrapper.configuration;

import backend.academy.scrapper.mapper.LinkMapper;
import backend.academy.scrapper.repository.ChatLinkRepository;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.service.orm.OrmChatService;
import backend.academy.scrapper.service.orm.OrmLinkService;
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

    @Bean
    LinkService linkService(LinkRepository linkRepository, ChatLinkRepository chatLinkRepository,
                            LinkMapper mapper, ChatService chatService) {
        return new OrmLinkService(linkRepository, chatLinkRepository, mapper, chatService);
    }
}
