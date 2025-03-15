// package dao;
//
// import backend.academy.scrapper.dao.link.LinkDao;
// import backend.academy.scrapper.dto.request.AddLinkRequest;
// import backend.academy.scrapper.entity.Link;
// import base.IntegrationTest;
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.transaction.annotation.Transactional;
// import java.net.URI;
// import java.time.OffsetDateTime;
// import java.util.List;
// import java.util.Optional;
//
// public class LinkDaoImplTest extends IntegrationTest {
//
//    @Autowired
//    LinkDao linkDao;
//
//    @DisplayName("Test: добавление ссылки")
//    @Transactional
//    @Test
//    public void addLink() {
//        AddLinkRequest request = new AddLinkRequest(
//            URI.create("https://github.com"),
//            List.of("tag1", "tag2"),
//            List.of("filter1", "filter2")
//        );
//
//        Long linkId = linkDao.addLink(request);
//        Assertions.assertNotNull(linkId);
//    }
//
//
//    @DisplayName("Test: получение ссылки по ID")
//    @Transactional
//    @Test
//    void findLinkByLinkId() {
//        AddLinkRequest request = new AddLinkRequest(
//            URI.create("https://github.com"),
//            List.of("tag1", "tag2"),
//            List.of("filter1", "filter2")
//        );
//        Long linkId = linkDao.addLink(request);
//
//        Optional<Link> linkOptional = linkDao.findLinkByLinkId(linkId);
//
//        Assertions.assertTrue(linkOptional.isPresent());
//        Link link = linkOptional.get();
//        Assertions.assertEquals(linkId, link.id());
//        Assertions.assertEquals(request.link().toString(), link.url());
//    }
//
//    @DisplayName("Test: удаление ссылки")
//    @Transactional
//    @Test
//    void remove() {
//        AddLinkRequest request = new AddLinkRequest(
//            URI.create("https://github.com"),
//            List.of("tag1", "tag2"),
//            List.of("filter1", "filter2")
//        );
//        Long linkId = linkDao.addLink(request);
//
//        linkDao.remove(linkId);
//
//        Optional<Link> linkOptional = linkDao.findLinkByLinkId(linkId);
//        Assertions.assertFalse(linkOptional.isPresent());
//    }
//
//    @DisplayName("Test: получение всех ссылок с пагинацией")
//    @Transactional
//    @Test
//    void getAllLinks() {
//        linkDao.addLink(new AddLinkRequest(URI.create("https://github.com/1"), List.of(), List.of()));
//        linkDao.addLink(new AddLinkRequest(URI.create("https://github.com/2"), List.of(), List.of()));
//        linkDao.addLink(new AddLinkRequest(URI.create("https://github.com/3"), List.of(), List.of()));
//
//        List<Link> links = linkDao.getAllLinks(0, 2);
//
//        Assertions.assertEquals(2, links.size());
//    }
//
//
//    @DisplayName("Test: обновление ссылки")
//    @Transactional
//    @Test
//    void update() {
//        AddLinkRequest request = new AddLinkRequest(
//            URI.create("https://github.com"),
//            List.of("tag1", "tag2"),
//            List.of("filter1", "filter2")
//        );
//        Long linkId = linkDao.addLink(request);
//
//        Link newLink = Link.builder()
//            .id(linkId)
//            .url("https://github.com/1")
//            .tags(List.of("java", "spring", "example"))
//            .filters(List.of("filter1", "filter2"))
//            .description("Some Description")
//            .updatedAt(OffsetDateTime.now())
//            .build();
//
//
//        linkDao.update(newLink);
//
//        Optional<Link> linkOptional = linkDao.findLinkByLinkId(linkId);
//        Assertions.assertTrue(linkOptional.isPresent());
//        Link link = linkOptional.get();
//        Assertions.assertNotNull(link.updatedAt());
//        Assertions.assertEquals(link.description(),newLink.description());
//    }
//
//    @DisplayName("Test: получение ссылок по списку ID")
//    @Transactional
//    @Test
//    void getLinkById() {
//        Long linkId1 = linkDao.addLink(new AddLinkRequest(URI.create("https://example1.com"), List.of(), List.of()));
//        Long linkId2 = linkDao.addLink(new AddLinkRequest(URI.create("https://example2.com"), List.of(), List.of()));
//
//        List<Link> links = linkDao.getLinkById(List.of(linkId1, linkId2));
//
//        Assertions.assertEquals(2, links.size());
//        Assertions.assertTrue(links.stream().anyMatch(link -> link.id().equals(linkId1)));
//        Assertions.assertTrue(links.stream().anyMatch(link -> link.id().equals(linkId2)));
//    }
// }
