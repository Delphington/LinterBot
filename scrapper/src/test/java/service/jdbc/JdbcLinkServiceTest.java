// package service.jdbc;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import backend.academy.scrapper.dto.request.AddLinkRequest;
// import backend.academy.scrapper.dto.response.LinkResponse;
// import backend.academy.scrapper.dto.response.ListLinksResponse;
// import backend.academy.scrapper.entity.Link;
// import backend.academy.scrapper.entity.TgChat;
// import backend.academy.scrapper.entity.TgChatLink;
// import backend.academy.scrapper.exception.chat.ChatNotExistException;
// import backend.academy.scrapper.exception.link.LinkAlreadyExistException;
// import backend.academy.scrapper.exception.link.LinkNotFoundException;
// import backend.academy.scrapper.repository.LinkRepository;
// import backend.academy.scrapper.repository.TgChatLinkRepository;
// import backend.academy.scrapper.repository.TgChatRepository;
// import backend.academy.scrapper.service.jdbc.JdbcLinkService;
// import base.IntegrationTest;
// import java.net.URI;
// import java.time.OffsetDateTime;
// import java.util.Collections;
// import java.util.List;
// import java.util.Optional;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
//
// class JdbcLinkServiceTest extends IntegrationTest {
//
//    @Autowired
//    private JdbcLinkService jdbcLinkService;
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
//    private final Long tgChatId = 1L;
//    private final URI uri = URI.create("https://example.com");
//    private final AddLinkRequest addLinkRequest =
//            new AddLinkRequest(uri, Collections.emptyList(), Collections.emptyList());
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
//    void findAllLinksByChatId_ShouldReturnListLinksResponse() {
//        // Arrange
//        Link link = new Link();
//        link.url(uri.toString());
//        link.description("description");
//        link.updatedAt(OffsetDateTime.now());
//        linkRepository.save(link);
//
//        TgChatLink tgChatLink = new TgChatLink();
//        tgChatLink.tgChat(tgChatRepository.findById(tgChatId).get());
//        tgChatLink.setLink(link);
//        tgChatLinkRepository.save(tgChatLink);
//
//        // Act
//        ListLinksResponse response = jdbcLinkService.findAllLinksByChatId(tgChatId);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(1, response.size());
//    }
//
//    @Test
//    void addLink_ShouldAddLinkAndReturnLinkResponse() {
//        LinkResponse response = jdbcLinkService.addLink(tgChatId, addLinkRequest);
//
//        assertNotNull(response);
//        assertEquals(uri, response.url());
//        assertTrue(linkRepository.findById(response.id()).isPresent());
//    }
//
//    @Test
//    void addLink_ShouldThrowLinkAlreadyExistException_WhenLinkAlreadyExists() {
//        jdbcLinkService.addLink(tgChatId, addLinkRequest);
//
//        assertThrows(LinkAlreadyExistException.class, () -> jdbcLinkService.addLink(tgChatId, addLinkRequest));
//    }
//
//    @Test
//    void deleteLink_ShouldDeleteLinkAndReturnLinkResponse() {
//        jdbcLinkService.addLink(tgChatId, addLinkRequest);
//        LinkResponse addedLink =
//                jdbcLinkService.findAllLinksByChatId(tgChatId).links().get(0);
//
//        LinkResponse response = jdbcLinkService.deleteLink(tgChatId, uri);
//
//        assertNotNull(response);
//        assertEquals(addedLink.id(), response.id());
//        assertFalse(linkRepository.findById(response.id()).isPresent());
//    }
//
//    @Test
//    void deleteLink_ShouldThrowChatNotExistException_WhenChatDoesNotExist() {
//        // Act & Assert
//        assertThrows(ChatNotExistException.class, () -> jdbcLinkService.deleteLink(999L, uri));
//    }
//
//    @Test
//    void deleteLink_ShouldThrowLinkNotFoundException_WhenLinkDoesNotExist() {
//        assertThrows(LinkNotFoundException.class, () -> jdbcLinkService.deleteLink(tgChatId, uri));
//    }
//
//    @Test
//    void findById_ShouldReturnLink_WhenLinkExists() {
//        // Arrange
//        jdbcLinkService.addLink(tgChatId, addLinkRequest);
//        LinkResponse addedLink =
//                jdbcLinkService.findAllLinksByChatId(tgChatId).links().get(0);
//
//        // Act
//        Optional<Link> result = jdbcLinkService.findById(addedLink.id());
//
//        // Assert
//        assertTrue(result.isPresent());
//        assertEquals(addedLink.id(), result.get().id());
//    }
//
//    @Test
//    void findById_ShouldReturnEmptyOptional_WhenLinkDoesNotExist() {
//        // Act
//        Optional<Link> result = jdbcLinkService.findById(999L);
//
//        // Assert
//        assertFalse(result.isPresent());
//    }
//
//    @Test
//    void findAllLinksByChatId_ShouldReturnListOfLinks() {
//        // Arrange
//        jdbcLinkService.addLink(tgChatId, addLinkRequest);
//
//        // Act
//        List<Link> result = jdbcLinkService.findAllLinksByChatId(0, 10);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    void update_ShouldUpdateLink() {
//        // Arrange
//        jdbcLinkService.addLink(tgChatId, addLinkRequest);
//        LinkResponse addedLink =
//                jdbcLinkService.findAllLinksByChatId(tgChatId).links().get(0);
//        Link updatedLink = new Link();
//        updatedLink.id(addedLink.id());
//        updatedLink.url(uri.toString());
//        updatedLink.description("updated description");
//        updatedLink.updatedAt(OffsetDateTime.now());
//
//        // Act
//        jdbcLinkService.update(updatedLink);
//
//        // Assert
//        Optional<Link> result = jdbcLinkService.findById(addedLink.id());
//        assertTrue(result.isPresent());
//        assertEquals("updated description", result.get().description());
//    }
// }
