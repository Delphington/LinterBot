package backend.academy.scrapper.api.service;

import backend.academy.scrapper.api.dto.request.AddLinkRequest;
import backend.academy.scrapper.api.dto.request.RemoveLinkRequest;
import backend.academy.scrapper.api.dto.response.LinkResponse;
import backend.academy.scrapper.api.dto.response.ListLinksResponse;
import backend.academy.scrapper.api.exception.LinkAlreadyExistException;
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
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
@Service
public class LinkService {

    private final LinkMapper mapper;


    private Map<Long, List<LinkResponse>> links = new HashMap<>();

    public ListLinksResponse getAllLinks(Long id) {
        return new ListLinksResponse(links.get(id), links.get(id).size());

    }

    public LinkResponse addLink(Long tgChatId, AddLinkRequest request) {
        if (!links.containsKey(tgChatId)) {
            // Проверка на то что существует ли такой чат
            log.error("FROM LinkService - addLink: не существует такого чата");
        }
        List<LinkResponse> linkList = links.getOrDefault(tgChatId, new ArrayList<>());

        //todo: мы идем по запросу и ищем ссылку, если ссылки нет -> добавляем
        //todo: если ссылка все же нашлась, то хз нужно сверить теги и сортивроку
        //todo: если все сошлось кинуть исключения -> сейчас я просто проверяю если
        //todo: ссылка есть -> exception -> на описание все равно


        if (isLinkExist(linkList, request.link())) {
            throw new LinkAlreadyExistException("Такая ссылка уже существует");
        }


        LinkResponse linkModel = mapper.AddLinkRequestToLinkResponse(request, tgChatId);

        linkList.add(linkModel);

        return linkModel;

    }


    public LinkResponse deleteLink(Long tgChatId, URI uri) {
        if (!links.containsKey(tgChatId)) {
            // Проверка на то что существует ли такой чат
            log.error("FROM LinkService - deleteLink: не существует такого чата");
        }
        List<LinkResponse> list = links.get(tgChatId);

        deleteUrl(list, uri);

        LinkResponse linkModel = new LinkResponse(tgChatId, uri, null, null);


        return linkModel;
    }

    private void deleteUrl(List<LinkResponse> linkList, URI uri) {
        Iterator<LinkResponse> iterator = linkList.iterator();
        while (iterator.hasNext()) {
            LinkResponse link = iterator.next();
            if (link.url().toString().equals(uri.toString())) {
                iterator.remove();
                break;
            }
        }
    }


    private boolean isChatExist(Long id) {
        return links.containsKey(id);
    }


    //проверяем uri по String на равность
    private boolean isLinkExist(List<LinkResponse> list, URI uri) {
        for (LinkResponse linkModel : list) {
            if (linkModel.url().toString().equals(uri.toString())) {
                return true;
            }
        }
        return false;
    }


}
