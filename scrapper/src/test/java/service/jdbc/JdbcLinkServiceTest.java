package service.jdbc;

import backend.academy.scrapper.service.jdbc.JdbcLinkService;
import base.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class JdbcLinkServiceTest extends IntegrationTest {

    @Autowired
    private JdbcLinkService jdbcLinkService;

    @Test
    public void findAllLinksByChatId() {

    }
}
/***

 @Slf4j
 @RequiredArgsConstructor
 @Service public class JdbcLinkService implements LinkService {

 private final ChatDao chatDao;
 private final LinkDao linkDao;
 private final ChatLinkDao chatLinkDao;

 private final LinkMapper mapper;

 @Override public ListLinksResponse getAllLinks(Long tgChatId) {
 if (!chatDao.isExistChat(tgChatId)) {
 log.error("Ошибка, пользователя не существует");
 throw new ChatNotExistException("Чат с ID " + tgChatId + " не найден.");
 }

 List<Long> linkIdsList = chatLinkDao.getLinkIdsByChatId(tgChatId);

 List<Link> linkList = linkDao.getLinkById(linkIdsList);

 log.info("LinkService: getAllLinks, id = {}", Utils.sanitize(tgChatId));

 return new ListLinksResponse(mapper.LinkListToLinkResponseList(linkList), linkList.size());
 }

 @Override public LinkResponse addLink(Long tgChatId, AddLinkRequest request) {
 log.info("Начало добавления ссылки для чата с ID: {}", tgChatId);

 if (!chatDao.isExistChat(tgChatId)) {
 log.error("Чат с ID {} не существует.", tgChatId);
 throw new ChatNotExistException("Чат с ID " + tgChatId + " не найден.");
 }
 log.info("Чат с ID {} существует.", tgChatId);

 //Все id ссылок пользователей
 List<Long> linkIdsList = chatLinkDao.getLinkIdsByChatId(tgChatId);
 log.info("Получен список ID ссылок для чата {}: {}", tgChatId, linkIdsList);

 List<Link> linkList = linkDao.getLinkById(linkIdsList);
 log.info("Получен список ссылок для чата {}: {}", tgChatId, linkList);



 if (findLinkByUrl(linkList, request.link().toString()).isPresent()) {
 log.warn("Ссылка {} уже существует для чата {}", request.link(), tgChatId);
 throw new LinkAlreadyExistException("Такая ссылка уже существует для этого чата");
 }
 log.info("Ссылка {} не найдена в существующих ссылках чата {}.", request.link(), tgChatId);



 Long idLink = linkDao.addLink(request);
 log.info("Добавлена новая ссылка с ID: {}", idLink);

 chatLinkDao.addRecord(tgChatId, idLink);
 log.info("Добавлена запись в ChatLink для чата {} и ссылки {}", tgChatId, idLink);

 LinkResponse linkResponse = new LinkResponse(idLink, request.link(), request.tags(), request.filters());
 log.info("Ссылка успешно добавлена и преобразована в LinkResponse: {}", linkResponse);

 log.info("Завершено добавление ссылки для чата с ID: {}", tgChatId);
 return linkResponse;
 }

 @Override public LinkResponse deleteLink(Long tgChatId, URI uri) {
 if (!chatDao.isExistChat(tgChatId)) {
 log.error("Чат с ID {} не существует.", tgChatId);
 throw new ChatNotExistException("Чат с ID " + tgChatId + " не найден.");
 }
 //Все id ссылок пользователей
 List<Long> linkIdsList = chatLinkDao.getLinkIdsByChatId(tgChatId);
 log.info("Получен список ID ссылок для чата {}: {}", tgChatId, linkIdsList);

 List<Link> linkList = linkDao.getLinkById(linkIdsList);
 log.info("Получен список ссылок для чата {}: {}", tgChatId, linkList);


 Optional<Link> linkExist = findLinkByUrl(linkList, uri.toString());

 if (linkExist.isEmpty()) {
 log.warn("Ссылка {} не существует для чата {}", uri, tgChatId);
 throw new LinkNotFoundException("Такая ссылка уже существует для этого чата");
 }

 linkDao.remove(linkExist.get().id());

 return mapper.LinkToLinkResponse(linkExist.get());
 }

 @Override public Optional<Link> findById(Long id) {
 return linkDao.findLinkByLinkId(id);
 }

 @Override public List<Link> getAllLinks(int offset, int limit) {
 return linkDao.getAllLinks(offset, limit);
 }

 @Override public void update(Link link) {
 linkDao.update(link);
 }

 @Override public ListLinksResponse getListLinkByTag(Long tgChatId, String tag) {
 if (!chatDao.isExistChat(tgChatId)) {
 log.error("Чат с ID {} не существует.", tgChatId);
 throw new ChatNotExistException("Чат с ID " + tgChatId + " не найден.");
 }

 List<Long> linkIdsList = chatLinkDao.getLinkIdsByChatId(tgChatId);

 List<Link> linkList = linkDao.getLinkById(linkIdsList);

 List<Link> filteredLinks = linkList.stream()
 .filter(link -> link.tags() != null && link.tags().contains(tag))
 .collect(Collectors.toList());

 List<LinkResponse> linkResponses = mapper.LinkListToLinkResponseList(filteredLinks);

 return new ListLinksResponse(linkResponses, linkResponses.size());
 }


 //-------------

 private Optional<Link> findLinkByUrl(List<Link> list, String url) {
 return list.stream()
 .filter(link -> link.url().equals(url))
 .findFirst();
 }
 }





  *
  * */
