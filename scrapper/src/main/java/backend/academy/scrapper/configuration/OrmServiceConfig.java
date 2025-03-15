package backend.academy.scrapper.configuration;

import backend.academy.scrapper.mapper.LinkMapper;
import backend.academy.scrapper.repository.ChatLinkRepository;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.FilterRepository;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.TagRepository;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.service.TagService;
import backend.academy.scrapper.service.orm.OrmChatService;
import backend.academy.scrapper.service.orm.OrmLinkService;
import backend.academy.scrapper.service.orm.OrmTagService;
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
                            LinkMapper mapper, ChatService chatService,
                            TagRepository tagRepository, FilterRepository filterRepository) {
        return new OrmLinkService(linkRepository, chatLinkRepository,
            mapper, chatService, tagRepository, filterRepository);
    }

    @Bean
    TagService tagService(LinkService linkService, ChatLinkRepository chatLinkRepository, LinkMapper linkMapper) {
        return new OrmTagService(linkService, chatLinkRepository, linkMapper);
    }
}
