// package service.orm;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import backend.academy.scrapper.dto.request.tag.TagRemoveRequest;
// import backend.academy.scrapper.entity.TgChat;
// import backend.academy.scrapper.exception.link.LinkNotFoundException;
// import backend.academy.scrapper.repository.LinkRepository;
// import backend.academy.scrapper.repository.TagRepository;
// import backend.academy.scrapper.repository.TgChatLinkRepository;
// import backend.academy.scrapper.repository.TgChatRepository;
// import backend.academy.scrapper.service.LinkService;
// import backend.academy.scrapper.service.orm.OrmLinkService;
// import backend.academy.scrapper.service.orm.OrmTagService;
// import base.IntegrationTest;
// import java.net.URI;
// import java.time.OffsetDateTime;
// import java.util.ArrayList;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.TestConfiguration;
// import org.springframework.context.annotation.Bean;
// import org.springframework.transaction.annotation.Transactional;
//
// class OrmTagServiceTest extends IntegrationTest {
//
//    @Autowired
//    private OrmTagService ormTagService;
//
//    @Autowired
//    private TgChatRepository tgChatRepository;
//
//    @Autowired
//    private LinkRepository linkRepository;
//
//    @Autowired
//    private TgChatLinkRepository tgChatLinkRepository;
//
//    @Autowired
//    private TagRepository tagRepository;
//
//    @Autowired
//    private LinkService linkService;
//
//    @TestConfiguration
//    static class TestConfig {
//        @Bean
//        public OrmLinkService ormLinkService() {
//            return Mockito.mock(OrmLinkService.class);
//        }
//    }
//
//    private final Long tgChatId = 1L;
//    private final URI uri = URI.create("https://example.com");
//    private final String tagName = "exampleTag";
//
//    @BeforeEach
//    void setUp() {
//        // Очистка базы данных перед каждым тестом
//        tgChatLinkRepository.deleteAll();
//        tagRepository.deleteAll();
//        linkRepository.deleteAll();
//        tgChatRepository.deleteAll();
//
//        // Добавление тестового чата
//        TgChat tgChat = new TgChat();
//        tgChat.id(tgChatId);
//        tgChat.createdAt(OffsetDateTime.now());
//        tgChat.tgChatLinks(new ArrayList<>()); // Инициализация коллекции
//        tgChatRepository.save(tgChat);
//    }
//
//    @Test
//    @Transactional
//    void removeTagFromLink_ShouldThrowLinkNotFoundException_WhenLinkDoesNotExist() {
//        // Arrange
//        TagRemoveRequest tagRemoveRequest = new TagRemoveRequest(tagName, uri);
//
//        // Act & Assert
//        assertThrows(LinkNotFoundException.class, () -> ormTagService.removeTagFromLink(tgChatId, tagRemoveRequest));
//    }
// }
