//package service.orm; // package service.orm;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import backend.academy.scrapper.dto.request.AddLinkRequest;
//import backend.academy.scrapper.dto.response.LinkResponse;
//import backend.academy.scrapper.entity.Link;
//import backend.academy.scrapper.entity.TgChat;
//import backend.academy.scrapper.exception.chat.ChatNotExistException;
//import backend.academy.scrapper.exception.link.LinkAlreadyExistException;
//import backend.academy.scrapper.exception.link.LinkNotFoundException;
//import backend.academy.scrapper.repository.LinkRepository;
//import backend.academy.scrapper.repository.TgChatLinkRepository;
//import backend.academy.scrapper.repository.TgChatRepository;
//import backend.academy.scrapper.service.ChatService;
//import backend.academy.scrapper.service.orm.OrmLinkService;
//import base.IntegrationTest;
//import java.net.URI;
//import java.time.OffsetDateTime;
//import java.util.List;
//import java.util.Optional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//class OrmLinkServiceTest extends IntegrationTest {
//
//    @Autowired
//    private OrmLinkService ormLinkService;
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
//    private ChatService chatService;
//
//    private final Long tgChatId = 1L;
//    private final URI uri = URI.create("https://example.com");
//    private final AddLinkRequest addLinkRequest = new AddLinkRequest(uri, List.of("tag1"), List.of("filter1"));
//
//    @BeforeEach
//    void setUp() {
//        // Очистка базы данных перед каждым тестом
//        tgChatLinkRepository.deleteAll();
//        linkRepository.deleteAll();
//        tgChatRepository.deleteAll();
//
//        // Добавление тестового чата
//        TgChat tgChat = new TgChat();
//        tgChat.id(tgChatId);
//        tgChat.createdAt(OffsetDateTime.now());
//        tgChatRepository.save(tgChat);
//    }
//
//    @Test
//    void addLink_ShouldAddLinkAndReturnLinkResponse() {
//        // Act
//        LinkResponse response = ormLinkService.addLink(tgChatId, addLinkRequest);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(uri, response.url());
//        assertTrue(linkRepository.findById(response.id()).isPresent());
//    }
//
//    @Test
//    void addLink_ShouldThrowLinkAlreadyExistException_WhenLinkAlreadyExists() {
//        // Arrange
//        ormLinkService.addLink(tgChatId, addLinkRequest);
//
//        // Act & Assert
//        assertThrows(LinkAlreadyExistException.class, () -> ormLinkService.addLink(tgChatId, addLinkRequest));
//    }
//
//    @Test
//    void addLink_ShouldThrowChatNotExistException_WhenChatDoesNotExist() {
//        // Arrange
//        tgChatRepository.deleteAll();
//
//        // Act & Assert
//        assertThrows(ChatNotExistException.class, () -> ormLinkService.addLink(tgChatId, addLinkRequest));
//    }
//
//    @Test
//    void deleteLink_ShouldDeleteLinkAndReturnLinkResponse() {
//        // Arrange
//        LinkResponse addedLink = ormLinkService.addLink(tgChatId, addLinkRequest);
//
//        // Act
//        LinkResponse response = ormLinkService.deleteLink(tgChatId, uri);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(addedLink.id(), response.id());
//        assertFalse(linkRepository.findById(response.id()).isPresent());
//    }
//
//    @Test
//    void deleteLink_ShouldThrowLinkNotFoundException_WhenLinkDoesNotExist() {
//        // Act & Assert
//        assertThrows(LinkNotFoundException.class, () -> ormLinkService.deleteLink(tgChatId, uri));
//    }
//
//    @Test
//    void findById_ShouldReturnLink_WhenLinkExists() {
//        // Arrange
//        LinkResponse addedLink = ormLinkService.addLink(tgChatId, addLinkRequest);
//
//        // Act
//        Optional<Link> result = ormLinkService.findById(addedLink.id());
//
//        // Assert
//        assertTrue(result.isPresent());
//        assertEquals(addedLink.id(), result.get().id());
//    }
//
//    @Test
//    void findById_ShouldReturnEmptyOptional_WhenLinkDoesNotExist() {
//        // Act
//        Optional<Link> result = ormLinkService.findById(999L);
//
//        // Assert
//        assertFalse(result.isPresent());
//    }
//
//    //    @Test
//    //    void findAllLinksByChatId_ShouldReturnListOfLinks() {
//    //        // Arrange
//    //        ormLinkService.addLink(tgChatId, addLinkRequest);
//    //
//    //        // Act
//    //        List<Link> result = ormLinkService.findAllLinksByChatId(0, 10);
//    //
//    //        // Assert
//    //        assertNotNull(result);
//    //        assertEquals(1, result.size());
//    //    }
//
//    @Test
//    void update_ShouldUpdateLink() {
//        // Arrange
//        LinkResponse addedLink = ormLinkService.addLink(tgChatId, addLinkRequest);
//        Link updatedLink = new Link();
//        updatedLink.id(addedLink.id());
//        updatedLink.url(uri.toString());
//        updatedLink.description("updated description");
//        updatedLink.updatedAt(OffsetDateTime.now());
//
//        // Act
//        ormLinkService.update(updatedLink);
//
//        // Assert
//        Optional<Link> result = ormLinkService.findById(addedLink.id());
//        assertTrue(result.isPresent());
//        assertEquals("updated description", result.get().description());
//    }
//}
