package backend.academy.scrapper.api.service;

import backend.academy.scrapper.api.dto.request.AddLinkRequest;
import backend.academy.scrapper.api.dto.response.LinkResponse;
import backend.academy.scrapper.api.dto.response.ListLinksResponse;
import backend.academy.scrapper.api.exception.link.LinkAlreadyExistException;
import backend.academy.scrapper.api.mapper.LinkMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.Response;
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
    //todo: по-хорошему надо объединить
    // два сервиса, чтобы создавалось хранилище при /start


    private final LinkMapper mapper;

    private Map<Long, List<LinkResponse>> repoLinks = new HashMap<>();

    public ListLinksResponse getAllLinks(Long id) {
        return new ListLinksResponse(repoLinks.get(id), repoLinks.get(id).size());

    }

    //мб проверку на null в tgChatID
    public LinkResponse addLink(Long tgChatId, AddLinkRequest request) {
        if (!repoLinks.containsKey(tgChatId)) {
            //Такого чата нет, ссылка будет добавлена первая
            repoLinks.put(tgChatId, new ArrayList<>());
        }

        List<LinkResponse> linkList = repoLinks.get(tgChatId);


        //todo: мы идем по запросу и ищем ссылку, если ссылки нет -> добавляем
        //todo: если ссылка все же нашлась, то хз нужно сверить теги и сортивроку
        //todo: если все сошлось кинуть исключения -> сейчас я просто проверяю если
        //todo: ссылка есть -> exception -> на описание все равно


        LinkResponse linkResponseFromRequest = mapper.AddLinkRequestToLinkResponse(request, tgChatId);

        Optional<LinkResponse> optional = searchLink(linkList, request.link());

        if (optional.isPresent()) {
            throw new LinkAlreadyExistException("Такая ссылка уже существует");
        }

        linkList.add(linkResponseFromRequest);
        log.info("LinkService: Ссылка была добавлена: " + linkResponseFromRequest.url().toString());
        return linkResponseFromRequest;


    }


    public LinkResponse deleteLink(Long tgChatId, URI uri) {
        if (!repoLinks.containsKey(tgChatId)) {
            // Проверка на то что существует ли такой чат
            log.error("FROM LinkService - deleteLink: не существует такого чата");
        }
        List<LinkResponse> list = repoLinks.get(tgChatId);

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
