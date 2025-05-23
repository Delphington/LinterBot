package backend.academy.scrapper.configuration.db;

import backend.academy.scrapper.mapper.FilterMapper;
import backend.academy.scrapper.mapper.LinkMapper;
import backend.academy.scrapper.repository.AccessFilterRepository;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.TgChatLinkRepository;
import backend.academy.scrapper.repository.TgChatRepository;
import backend.academy.scrapper.service.AccessFilterService;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.service.TagService;
import backend.academy.scrapper.service.orm.OrmAccessFilterService;
import backend.academy.scrapper.service.orm.OrmChatService;
import backend.academy.scrapper.service.orm.OrmLinkService;
import backend.academy.scrapper.service.orm.OrmTagService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "orm")
public class OrmServiceConfig {

    @Bean
    @Profile("orm")
    ChatService chatService(TgChatRepository tgChatRepository) {
        return new OrmChatService(tgChatRepository);
    }

    @Bean
    @Profile("orm")
    LinkService linkService(
            LinkRepository linkRepository,
            TgChatLinkRepository tgChatLinkRepository,
            LinkMapper mapper,
            ChatService chatService) {
        return new OrmLinkService(linkRepository, tgChatLinkRepository, mapper, chatService);
    }

    @Bean
    @Profile("orm")
    TagService tagService(LinkService linkService, TgChatLinkRepository tgChatLinkRepository, LinkMapper linkMapper) {
        return new OrmTagService(linkService, tgChatLinkRepository, linkMapper);
    }

    @Bean
    @Profile("orm")
    AccessFilterService accessFilterService(
            AccessFilterRepository accessFilterRepository,
            TgChatRepository tgChatRepository,
            FilterMapper filterMapper) {
        return new OrmAccessFilterService(tgChatRepository, accessFilterRepository, filterMapper);
    }
}
