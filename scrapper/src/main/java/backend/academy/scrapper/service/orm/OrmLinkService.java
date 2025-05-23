package backend.academy.scrapper.service.orm;

import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.entity.AccessFilter;
import backend.academy.scrapper.entity.Filter;
import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.entity.Tag;
import backend.academy.scrapper.entity.TgChat;
import backend.academy.scrapper.entity.TgChatLink;
import backend.academy.scrapper.exception.chat.ChatNotExistException;
import backend.academy.scrapper.exception.link.LinkAlreadyExistException;
import backend.academy.scrapper.exception.link.LinkNotFoundException;
import backend.academy.scrapper.mapper.LinkMapper;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.TgChatLinkRepository;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.util.Utils;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrmLinkService implements LinkService {

    /** Проверка на chatId пользователя не проводится, так как считаем что данные приходят консистентные */
    private final LinkRepository linkRepository;

    private final TgChatLinkRepository tgChatLinkRepository;
    private final LinkMapper mapper;
    private final ChatService chatService;

    @Transactional(readOnly = true)
    @Override
    public ListLinksResponse findAllLinksByChatId(Long tgChatId) {
        log.info("LinkService: getAllLinks, chatId = {}", Utils.sanitize(tgChatId));
        List<Link> linkList = tgChatLinkRepository.findLinksByChatId(tgChatId);
        return new ListLinksResponse(mapper.linkListToLinkResponseList(linkList), linkList.size());
    }

    @Transactional
    @Override
    public LinkResponse addLink(Long tgChatId, AddLinkRequest request) {

        TgChat existingTgChat = chatService
                .findChatById(tgChatId)
                .orElseThrow(() -> new ChatNotExistException("Чат с ID " + tgChatId + " не найден."));

        if (tgChatLinkRepository
                .findByChatIdAndLinkUrl(tgChatId, request.link().toString())
                .isPresent()) {
            throw new LinkAlreadyExistException("Такая ссылка уже существует для этого чата");
        }

        Link newLink = new Link();
        newLink.url(request.link().toString());

        List<Tag> tags = request.tags().stream()
                .map(tagName -> Tag.create(tagName, newLink))
                .collect(Collectors.toList());
        newLink.tags(tags);

        List<Filter> filters = request.filters().stream()
                .map(filterValue -> Filter.create(filterValue, newLink))
                .collect(Collectors.toList());
        newLink.filters(filters);

        Link savedLink = linkRepository.save(newLink);

        TgChatLink tgChatLink = new TgChatLink();
        tgChatLink.setChat(existingTgChat);
        tgChatLink.link(savedLink);
        tgChatLinkRepository.save(tgChatLink);

        existingTgChat.tgChatLinks().add(tgChatLink);

        return mapper.linkToLinkResponse(savedLink);
    }

    @Transactional
    @Override
    public LinkResponse deleteLink(Long tgChatId, URI uri) {
        // Проверка существования связи между чатом и ссылкой
        Optional<TgChatLink> existingChatLink = tgChatLinkRepository.findByChatIdAndLinkUrl(tgChatId, uri.toString());
        if (existingChatLink.isEmpty()) {
            log.warn("Ссылка {} не найдена в чате {}", uri, tgChatId);
            throw new LinkNotFoundException("Ссылка " + uri + " не найдена в чате с ID " + tgChatId + ".");
        }

        TgChatLink tgChatLinkToDelete =
                existingChatLink.orElseThrow(() -> new LinkNotFoundException("Ссылка  не найдена"));
        Link linkResponse = tgChatLinkToDelete.link();
        tgChatLinkRepository.delete(tgChatLinkToDelete);
        log.info("Удалена связь между чатом {} и ссылкой {}", tgChatId, uri);

        // Проверка, остались ли другие связи с этой ссылкой
        if (tgChatLinkRepository.countByLinkId(linkResponse.id()) == 0) {
            // Если нет других связей, удаляем и саму ссылку
            linkRepository.delete(linkResponse);
            log.info("Ссылка {} удалена, так как больше не связана ни с одним чатом.", linkResponse.url());
        } else {
            log.info("Ссылка {} не удалена, так как связана с другими чатами.", linkResponse.url());
        }

        // Возвращаем ответ
        return mapper.linkToLinkResponse(linkResponse);
    }

    // Для scheduler
    @Transactional(readOnly = true)
    @Override
    public Optional<Link> findById(Long id) {
        return linkRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Link> findAllLinksByChatIdWithFilter(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);

        List<Link> list = linkRepository.findAll(pageable).getContent();

        List<Link> listWithFilter = new ArrayList<>();

        for (Link item : list) {
            List<TgChatLink> tgChatLinkList = item.tgChatLinks();
            for (TgChatLink itemTgChat : tgChatLinkList) {
                if (!isCompareFilters(item.filters(), itemTgChat.tgChat().accessFilters())) {
                    listWithFilter.add(item);
                }
            }
        }
        return listWithFilter;
    }

    private boolean isCompareFilters(List<Filter> filtersList, List<AccessFilter> accessFilterList) {
        for (AccessFilter accessFilter : accessFilterList) {
            for (Filter filter : filtersList) {
                if (accessFilter.filter().equals(filter.filter())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Transactional
    @Override
    public void update(Link link) {
        linkRepository.save(link);
    }
}
