package backend.academy.scrapper.api.service;

import backend.academy.scrapper.api.dto.request.AddLinkRequest;
import backend.academy.scrapper.api.dto.response.LinkResponse;
import backend.academy.scrapper.api.dto.response.ListLinksResponse;
import backend.academy.scrapper.api.exception.link.LinkAlreadyExistException;
import backend.academy.scrapper.api.exception.link.LinkNotFoundException;
import backend.academy.scrapper.api.mapper.LinkMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
@Service
public class LinkService {

    //todo: проверка, что взаимодействие начинается с /start

    private final LinkMapper mapper;

    private Map<Long, List<LinkResponse>> repoLinks = new HashMap<>();

    public void createAccount(Long tgChatId) {
        repoLinks.put(tgChatId, new ArrayList<>());
    }

    public ListLinksResponse getAllLinks(Long id) {
        log.info("===LinkService: getAllLinks, id = {}", id);
        return new ListLinksResponse(repoLinks.get(id), repoLinks.get(id).size());
    }

    public LinkResponse addLink(Long tgChatId, AddLinkRequest request) {

        List<LinkResponse> linkList = repoLinks.get(tgChatId);

        LinkResponse linkResponseFromRequest = mapper.AddLinkRequestToLinkResponse(request, tgChatId);

        Optional<LinkResponse> optional = searchLink(linkList, request.link());

        if (optional.isPresent()) {
            throw new LinkAlreadyExistException("Такая ссылка уже существует");
        }

        linkList.add(linkResponseFromRequest);
        log.info("===LinkService: addLink, id = {}, url = {}", tgChatId, linkResponseFromRequest.url().toString());

        return linkResponseFromRequest;
    }

    //Проверка существует ли вообще такой чат
    public LinkResponse deleteLink(Long tgChatId, URI uri) {
        List<LinkResponse> list = repoLinks.get(tgChatId);
        Optional<LinkResponse> optional = deleteUrl(list, uri);

        if (optional.isEmpty()) {
            throw new LinkNotFoundException("Ссылка не найдена");
        }

        log.info("===LinkService: deleteLink, id = {}, url = {}", tgChatId, uri.toString());

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


    //-------------------------------------------------------------

    private boolean isChatExist(Long id) {
        return repoLinks.containsKey(id);
    }


    private void updateComponentsLinkResponse(LinkResponse l1, LinkResponse l2) {

    }


    private boolean equalsComponentsLinkResponse(LinkResponse l1, LinkResponse l2) {
        return Objects.equals(l1.filters(), l2.filters())
               && Objects.equals(l1.tags(), l2.tags());
    }


    //проверяем uri по String, что uri в БД
    private Optional<LinkResponse> searchLink(List<LinkResponse> list, URI uri) {
        for (LinkResponse linkModel : list) {
            if (linkModel.url().toString().equals(uri.toString())) {
                return Optional.of(linkModel);
            }
        }
        return Optional.empty();
    }


}
