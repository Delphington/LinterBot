package backend.academy.scrapper.api.service;

import backend.academy.scrapper.api.dto.request.AddLinkRequest;
import backend.academy.scrapper.api.dto.response.LinkResponse;
import backend.academy.scrapper.api.dto.response.ListLinksResponse;
import backend.academy.scrapper.api.exception.link.LinkAlreadyExistException;
import backend.academy.scrapper.api.exception.link.LinkNotFoundException;
import backend.academy.scrapper.api.mapper.LinkMapper;
import backend.academy.scrapper.tracker.update.service.UpdateLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Log4j2
@Service
public class LinkService {

    //todo: проверка, что взаимодействие начинается с /start

    private final LinkMapper mapper;

    private static Long GENERATED_LINK_ID = 1L;

    // ID - пользователя: Ссылка
    private Map<Long, List<LinkResponse>> repoLinks = new ConcurrentHashMap<>();

    //Сервис для отслеживания обновлений
    private final UpdateLinkService updateLinkService;

    public void createAccount(Long tgChatId) {
        repoLinks.put(tgChatId, new ArrayList<>());
    }

    public ListLinksResponse getAllLinks(Long tgChatId) {
        log.info("LinkService: getAllLinks, id = {}", tgChatId);
        return new ListLinksResponse(repoLinks.get(tgChatId), repoLinks.get(tgChatId).size());
    }

    public LinkResponse addLink(Long tgChatId, AddLinkRequest request) {

        List<LinkResponse> linkList = repoLinks.get(tgChatId);

        LinkResponse linkResponseFromRequest = mapper.AddLinkRequestToLinkResponse(request, GENERATED_LINK_ID++);

        Optional<LinkResponse> optional = searchLinkByURI(linkList, request.link());

        if (optional.isPresent()) {
            throw new LinkAlreadyExistException("Такая ссылка уже существует");
        }

        linkList.add(linkResponseFromRequest);
        log.info("LinkService: addLink, id = {}, url = {}", tgChatId, linkResponseFromRequest.url().toString());

        updateLinkService.addLink(linkResponseFromRequest);
        return linkResponseFromRequest;
    }

    //Проверка существует ли вообще такой чат
    public LinkResponse deleteLink(Long tgChatId, URI uri) {
        List<LinkResponse> list = repoLinks.get(tgChatId);
        Optional<LinkResponse> optional = deleteUrl(list, uri);

        if (optional.isEmpty()) {
            throw new LinkNotFoundException("Ссылка не найдена");
        }

        log.info("LinkService: deleteLink, id = {}, url = {}", tgChatId, uri.toString());

        updateLinkService.deleteLink(optional.get());

        return optional.get();
    }


    private Optional<LinkResponse> deleteUrl(List<LinkResponse> linkList, URI uri) {
        if (linkList == null) {
            throw new LinkNotFoundException("Ссылка не найдена");
        }

        Iterator<LinkResponse> iterator = linkList.iterator();
        while (iterator.hasNext()) {
            LinkResponse link = iterator.next();
            if (link.url().toString().equals(uri.toString())) {
                iterator.remove();
                return Optional.of(link);
            }
        }
        return Optional.empty();
    }


    public List<Long> findIdChatsByUrlId(Long id) {
        List<Long> chatIds = new ArrayList<>();

        for (Map.Entry<Long, List<LinkResponse>> entry : repoLinks.entrySet()) {
            List<LinkResponse> links = entry.getValue();
            for (LinkResponse link : links) {
                if (link.id().equals(id)) {
                    chatIds.add(entry.getKey());
                }
            }
        }
        return chatIds;
    }


    //-------------------------------------------------------------

    //проверяем uri по String, что uri в БД
    private Optional<LinkResponse> searchLinkByURI(List<LinkResponse> list, URI uri) {
        for (LinkResponse linkModel : list) {
            if (linkModel.url().toString().equals(uri.toString())) {
                return Optional.of(linkModel);
            }
        }
        return Optional.empty();
    }
}
