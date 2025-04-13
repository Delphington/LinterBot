package backend.academy.scrapper.service.jdbc;

import backend.academy.scrapper.dao.TgChatLinkDao;
import backend.academy.scrapper.dao.chat.TgChatDao;
import backend.academy.scrapper.dao.link.LinkDao;
import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.exception.chat.ChatNotExistException;
import backend.academy.scrapper.exception.link.LinkAlreadyExistException;
import backend.academy.scrapper.exception.link.LinkNotFoundException;
import backend.academy.scrapper.mapper.LinkMapper;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.util.Utils;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {

    private final TgChatDao tgChatDao;
    private final LinkDao linkDao;
    private final TgChatLinkDao tgChatLinkDao;

    private final LinkMapper mapper;

    @Override
    public ListLinksResponse findAllLinksByChatId(Long tgChatId) {

        List<Long> linkIdsList = tgChatLinkDao.getLinkIdsByChatId(tgChatId);

        List<Link> linkList = linkDao.getListLinksByListLinkId(linkIdsList);

        log.info("LinkService: getAllLinks, chatId = {}", Utils.sanitize(tgChatId));

        return new ListLinksResponse(mapper.linkListToLinkResponseList(linkList), linkList.size());
    }

    //todo
    @Override
    public List<Link> findAllLinksByChatIdWithFilter(int offset, int batchSize) {
        log.info("findAllLinksByChatIdWithFilter, offset = {}, batchSize = {}", offset, batchSize);
        return linkDao.findAllLinksByChatIdWithFilter(offset, batchSize);
    }

    @Override
    public LinkResponse addLink(Long tgChatId, AddLinkRequest request) {
        // Все chatId ссылок пользователей
        List<Long> linkIdsList = tgChatLinkDao.getLinkIdsByChatId(tgChatId);
        List<Link> linkList = linkDao.getListLinksByListLinkId(linkIdsList);

        if (findLinkByUrl(linkList, request.link().toString()).isPresent()) {
            log.warn("Ссылка {} уже существует для чата {}", request.link(), tgChatId);
            throw new LinkAlreadyExistException("Такая ссылка уже существует для этого чата");
        }

        Long idLink = linkDao.addLink(request);
        tgChatLinkDao.addRecord(tgChatId, idLink);
        LinkResponse linkResponse = new LinkResponse(idLink, request.link(), request.tags(), request.filters());

        log.info("Завершено добавление ссылки для чата с ID: {}", tgChatId);
        return linkResponse;
    }

    @Override
    public LinkResponse deleteLink(Long tgChatId, URI uri) {
        if (!tgChatDao.isExistChat(tgChatId)) {
            log.error("Чат с ID {} не существует.", tgChatId);
            throw new ChatNotExistException("Чат с ID " + tgChatId + " не найден.");
        }
        // Все chatId ссылок пользователей
        List<Long> linkIdsList = tgChatLinkDao.getLinkIdsByChatId(tgChatId);
        List<Link> linkList = linkDao.getListLinksByListLinkId(linkIdsList);

        // Поиск ссылки по URL
        Link link = findLinkByUrl(linkList, uri.toString()).orElseThrow(() -> {
            log.warn("Ссылка {} не существует для чата {}", uri, tgChatId);
            return new LinkNotFoundException("Такая ссылка уже существует для этого чата");
        });

        // Удаление ссылки
        linkDao.remove(link.id());

        return mapper.linkToLinkResponse(link);
    }

    @Override
    public Optional<Link> findById(Long id) {
        return linkDao.findLinkByLinkId(id);
    }

    @Override
    public List<Link> findAllLinksByChatId(int offset, int limit) {
        return linkDao.getAllLinks(offset, limit);
    }

    @Override
    public void update(Link link) {
        linkDao.update(link);
    }

    private Optional<Link> findLinkByUrl(List<Link> list, String url) {
        return list.stream().filter(link -> link.url().equals(url)).findFirst();
    }
}
