package service.jdbc;

import backend.academy.scrapper.service.jdbc.JdbcLinkService;
import base.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class JdbcLinkServiceTest extends IntegrationTest {

    @Autowired
    private JdbcLinkService jdbcLinkService;

    @Test
    public void findAllLinksByChatId() {}
}
/**
 * * @Slf4j @RequiredArgsConstructor @Service public class JdbcLinkService implements LinkService {
 *
 * <p>private final ChatDao chatDao; private final LinkDao linkDao; private final ChatLinkDao chatLinkDao;
 *
 * <p>private final LinkMapper mapper; @Override public ListLinksResponse getAllLinks(Long tgChatId) { if
 * (!chatDao.isExistChat(tgChatId)) { log.error("Ошибка, пользователя не существует"); throw new
 * ChatNotExistException("Чат с ID " + tgChatId + " не найден."); }
 *
 * <p>List<Long> linkIdsList = chatLinkDao.getLinkIdsByChatId(tgChatId);
 *
 * <p>List<Link> linkList = linkDao.getLinkById(linkIdsList);
 *
 * <p>log.info("LinkService: getAllLinks, id = {}", Utils.sanitize(tgChatId));
 *
 * <p>return new ListLinksResponse(mapper.LinkListToLinkResponseList(linkList), linkList.size()); } @Override public
 * LinkResponse addLink(Long tgChatId, AddLinkRequest request) { log.info("Начало добавления ссылки для чата с ID: {}",
 * tgChatId);
 *
 * <p>if (!chatDao.isExistChat(tgChatId)) { log.error("Чат с ID {} не существует.", tgChatId); throw new
 * ChatNotExistException("Чат с ID " + tgChatId + " не найден."); } log.info("Чат с ID {} существует.", tgChatId);
 *
 * <p>//Все id ссылок пользователей List<Long> linkIdsList = chatLinkDao.getLinkIdsByChatId(tgChatId); log.info("Получен
 * список ID ссылок для чата {}: {}", tgChatId, linkIdsList);
 *
 * <p>List<Link> linkList = linkDao.getLinkById(linkIdsList); log.info("Получен список ссылок для чата {}: {}",
 * tgChatId, linkList);
 *
 * <p>if (findLinkByUrl(linkList, request.link().toString()).isPresent()) { log.warn("Ссылка {} уже существует для чата
 * {}", request.link(), tgChatId); throw new LinkAlreadyExistException("Такая ссылка уже существует для этого чата"); }
 * log.info("Ссылка {} не найдена в существующих ссылках чата {}.", request.link(), tgChatId);
 *
 * <p>Long idLink = linkDao.addLink(request); log.info("Добавлена новая ссылка с ID: {}", idLink);
 *
 * <p>chatLinkDao.addRecord(tgChatId, idLink); log.info("Добавлена запись в ChatLink для чата {} и ссылки {}", tgChatId,
 * idLink);
 *
 * <p>LinkResponse linkResponse = new LinkResponse(idLink, request.link(), request.tags(), request.filters());
 * log.info("Ссылка успешно добавлена и преобразована в LinkResponse: {}", linkResponse);
 *
 * <p>log.info("Завершено добавление ссылки для чата с ID: {}", tgChatId); return linkResponse; } @Override public
 * LinkResponse deleteLink(Long tgChatId, URI uri) { if (!chatDao.isExistChat(tgChatId)) { log.error("Чат с ID {} не
 * существует.", tgChatId); throw new ChatNotExistException("Чат с ID " + tgChatId + " не найден."); } //Все id ссылок
 * пользователей List<Long> linkIdsList = chatLinkDao.getLinkIdsByChatId(tgChatId); log.info("Получен список ID ссылок
 * для чата {}: {}", tgChatId, linkIdsList);
 *
 * <p>List<Link> linkList = linkDao.getLinkById(linkIdsList); log.info("Получен список ссылок для чата {}: {}",
 * tgChatId, linkList);
 *
 * <p>Optional<Link> linkExist = findLinkByUrl(linkList, uri.toString());
 *
 * <p>if (linkExist.isEmpty()) { log.warn("Ссылка {} не существует для чата {}", uri, tgChatId); throw new
 * LinkNotFoundException("Такая ссылка уже существует для этого чата"); }
 *
 * <p>linkDao.remove(linkExist.get().id());
 *
 * <p>return mapper.LinkToLinkResponse(linkExist.get()); } @Override public Optional<Link> findById(Long id) { return
 * linkDao.findLinkByLinkId(id); } @Override public List<Link> getAllLinks(int offset, int limit) { return
 * linkDao.getAllLinks(offset, limit); } @Override public void update(Link link) { linkDao.update(link); } @Override
 * public ListLinksResponse getListLinkByTag(Long tgChatId, String tag) { if (!chatDao.isExistChat(tgChatId)) {
 * log.error("Чат с ID {} не существует.", tgChatId); throw new ChatNotExistException("Чат с ID " + tgChatId + " не
 * найден."); }
 *
 * <p>List<Long> linkIdsList = chatLinkDao.getLinkIdsByChatId(tgChatId);
 *
 * <p>List<Link> linkList = linkDao.getLinkById(linkIdsList);
 *
 * <p>List<Link> filteredLinks = linkList.stream() .filter(link -> link.tags() != null && link.tags().contains(tag))
 * .collect(Collectors.toList());
 *
 * <p>List<LinkResponse> linkResponses = mapper.LinkListToLinkResponseList(filteredLinks);
 *
 * <p>return new ListLinksResponse(linkResponses, linkResponses.size()); }
 *
 * <p>//-------------
 *
 * <p>private Optional<Link> findLinkByUrl(List<Link> list, String url) { return list.stream() .filter(link ->
 * link.url().equals(url)) .findFirst(); } }
 */
