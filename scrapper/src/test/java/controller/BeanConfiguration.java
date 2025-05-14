package controller;

import backend.academy.scrapper.service.AccessFilterService;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.service.TagService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class BeanConfiguration {

    @Bean
    public ChatService chatService() {
        return Mockito.mock(ChatService.class);
    }

    @Bean
    public LinkService linkService() {
        return Mockito.mock(LinkService.class);
    }

    @Bean
    public AccessFilterService accessFilterService() {
        return Mockito.mock(AccessFilterService.class);
    }

    @Bean
    public TagService tagService() {
        return Mockito.mock(TagService.class);
    }
}
