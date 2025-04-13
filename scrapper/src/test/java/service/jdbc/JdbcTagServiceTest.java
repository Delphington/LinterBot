// package service.jdbc;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import backend.academy.scrapper.dto.request.tag.TagRemoveRequest;
// import backend.academy.scrapper.dto.response.LinkResponse;
// import backend.academy.scrapper.dto.response.ListLinksResponse;
// import backend.academy.scrapper.dto.response.TagListResponse;
// import backend.academy.scrapper.entity.Link;
// import backend.academy.scrapper.entity.Tag;
// import backend.academy.scrapper.entity.TgChat;
// import backend.academy.scrapper.entity.TgChatLink;
// import backend.academy.scrapper.exception.link.LinkNotFoundException;
// import backend.academy.scrapper.exception.tag.TagNotExistException;
// import backend.academy.scrapper.repository.LinkRepository;
// import backend.academy.scrapper.repository.TagRepository;
// import backend.academy.scrapper.repository.TgChatLinkRepository;
// import backend.academy.scrapper.repository.TgChatRepository;
// import backend.academy.scrapper.service.jdbc.JdbcTagService;
// import base.IntegrationTest;
// import java.net.URI;
// import java.time.OffsetDateTime;
// import java.time.ZoneId;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
//
// class JdbcTagServiceTest extends IntegrationTest {
//
//    @Autowired
//    private JdbcTagService jdbcTagService;
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
//        tgChat.createdAt(OffsetDateTime.now(ZoneId.systemDefault()));
//        tgChatRepository.save(tgChat);
//    }
//
//    @Test
//    void getListLinkByTag_ShouldReturnListLinksResponse() {
//        // Arrange
//        Link link = new Link();
//        link.url(uri.toString());
//        link.description("description");
//        link.updatedAt(OffsetDateTime.now(ZoneId.systemDefault()));
//        linkRepository.save(link);
//
//        TgChatLink tgChatLink = new TgChatLink();
//        tgChatLink.tgChat(tgChatRepository.findById(tgChatId).get());
//        tgChatLink.setLink(link);
//        tgChatLinkRepository.save(tgChatLink);
//
//        Tag tag = new Tag();
//        tag.link(link);
//        tag.tag(tagName);
//        tagRepository.save(tag);
//
//        // Act
//        ListLinksResponse response = jdbcTagService.getListLinkByTag(tgChatId, tagName);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(1, response.size());
//    }
//
//    @Test
//    void getAllListLinks_ShouldReturnTagListResponse() {
//        // Arrange
//        Link link = new Link();
//        link.url(uri.toString());
//        link.description("description");
//        link.updatedAt(OffsetDateTime.now(ZoneId.systemDefault()));
//        linkRepository.save(link);
//
//        TgChatLink tgChatLink = new TgChatLink();
//        tgChatLink.tgChat(tgChatRepository.findById(tgChatId).get());
//        tgChatLink.setLink(link);
//        tgChatLinkRepository.save(tgChatLink);
//
//        Tag tag = new Tag();
//        tag.link(link);
//        tag.tag(tagName);
//        tagRepository.save(tag);
//
//        // Act
//        TagListResponse response = jdbcTagService.getAllListLinks(tgChatId);
//
//        // Assert
//        assertNotNull(response);
//        assertTrue(response.tags().contains(tagName));
//    }
//
//    @Test
//    void removeTagFromLink_ShouldRemoveTagAndReturnLinkResponse() {
//        // Arrange
//        Link link = new Link();
//        link.url(uri.toString());
//        link.description("description");
//        link.updatedAt(OffsetDateTime.now(ZoneId.systemDefault()));
//        linkRepository.save(link);
//
//        TgChatLink tgChatLink = new TgChatLink();
//        tgChatLink.tgChat(tgChatRepository.findById(tgChatId).get());
//        tgChatLink.setLink(link);
//        tgChatLinkRepository.save(tgChatLink);
//
//        Tag tag = new Tag();
//        tag.link(link);
//        tag.tag(tagName);
//        tagRepository.save(tag);
//
//        TagRemoveRequest tagRemoveRequest = new TagRemoveRequest(tagName, uri);
//
//        // Act
//        LinkResponse response = jdbcTagService.removeTagFromLink(tgChatId, tagRemoveRequest);
//
//        // Assert
//        assertNotNull(response);
//    }
//
//    @Test
//    void removeTagFromLink_ShouldThrowLinkNotFoundException_WhenLinkDoesNotExist() {
//        // Arrange
//        TagRemoveRequest tagRemoveRequest = new TagRemoveRequest(tagName, uri);
//
//        // Act & Assert
//        assertThrows(LinkNotFoundException.class, () -> jdbcTagService.removeTagFromLink(tgChatId, tagRemoveRequest));
//    }
//
//    @Test
//    void removeTagFromLink_ShouldThrowTagNotExistException_WhenTagDoesNotExist() {
//        // Arrange
//        Link link = new Link();
//        link.url(uri.toString());
//        link.description("description");
//        link.updatedAt(OffsetDateTime.now(ZoneId.systemDefault()));
//        linkRepository.save(link);
//
//        TgChatLink tgChatLink = new TgChatLink();
//        tgChatLink.tgChat(tgChatRepository.findById(tgChatId).get());
//        tgChatLink.setLink(link);
//        tgChatLinkRepository.save(tgChatLink);
//
//        TagRemoveRequest tagRemoveRequest = new TagRemoveRequest("nonExistingTag", uri);
//
//        // Act & Assert
//        assertThrows(TagNotExistException.class, () -> jdbcTagService.removeTagFromLink(tgChatId, tagRemoveRequest));
//    }
// }
