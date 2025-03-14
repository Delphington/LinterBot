package backend.academy.scrapper.service.orm;

import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.entity.Filter;
import backend.academy.scrapper.entity.Tag;
import backend.academy.scrapper.entity.TgChat;
import backend.academy.scrapper.entity.TgChatLink;
import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.exception.chat.ChatNotExistException;
import backend.academy.scrapper.exception.link.LinkAlreadyExistException;
import backend.academy.scrapper.exception.link.LinkNotFoundException;
import backend.academy.scrapper.mapper.LinkMapper;
import backend.academy.scrapper.repository.ChatLinkRepository;
import backend.academy.scrapper.repository.FilterRepository;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.TagRepository;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.util.Utils;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import io.micrometer.core.instrument.Tags;
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

    /**
     *  Проверка на id пользователя не проводится,
     *  так как считаем что данные приходят консистентные
     */

    private final LinkRepository linkRepository;
    private final ChatLinkRepository chatLinkRepository;
    private final LinkMapper mapper;
    private final ChatService chatService;
    private final TagRepository tagRepository;
    private final FilterRepository filterRepository;

    @Transactional(readOnly = true)
    @Override
    public ListLinksResponse getAllLinks(Long tgChatId) {
        log.info("LinkService: getAllLinks, id = {}", Utils.sanitize(tgChatId));
        List<Link> linkList = chatLinkRepository.findLinksByChatId(tgChatId);
        return new ListLinksResponse(mapper.LinkListToLinkResponseList(linkList), linkList.size());
    }


    @Transactional
    @Override
    public LinkResponse addLink(Long tgChatId, AddLinkRequest request) {

        TgChat existingTgChat = chatService.findChatById(tgChatId)
            .orElseThrow(() -> new ChatNotExistException("Чат с ID " + tgChatId + " не найден."));

        if (chatLinkRepository.findByChatIdAndLinkUrl(tgChatId, request.link().toString()).isPresent()) {
            throw new LinkAlreadyExistException("Такая ссылка уже существует для этого чата");
        }

        Link newLink = new Link();
        newLink.url(request.link().toString());

        List<Tag> tags = request.tags().stream()
            .map(tagName -> {
                Tag tag = new Tag();
                tag.tag(tagName);
                tag.link(newLink);
                return tag;
            })
            .collect(Collectors.toList());
        newLink.tags(tags);

        List<Filter> filters = request.filters().stream()
            .map(filterValue -> {
                Filter filter = new Filter();
                filter.filter(filterValue);
                filter.link(newLink);
                return filter;
            })
            .collect(Collectors.toList());
        newLink.filters(filters);

        Link savedLink = linkRepository.save(newLink);

        TgChatLink tgChatLink = new TgChatLink();
        tgChatLink.setChat(existingTgChat);
        tgChatLink.link(savedLink);
        chatLinkRepository.save(tgChatLink);

        existingTgChat.tgChatLinks().add(tgChatLink);

        return mapper.LinkToLinkResponse(savedLink);
    }

    @Transactional
    @Override
    public LinkResponse deleteLink(Long tgChatId, URI uri) {
        // Проверка существования связи между чатом и ссылкой
        Optional<TgChatLink> existingChatLink = chatLinkRepository.findByChatIdAndLinkUrl(tgChatId, uri.toString());
        if (existingChatLink.isEmpty()) {
            log.warn("Ссылка {} не найдена в чате {}", uri, tgChatId);
            throw new LinkNotFoundException("Ссылка " + uri + " не найдена в чате с ID " + tgChatId + ".");
        }

        // Удаление связи между чатом и ссылкой
        TgChatLink tgChatLinkToDelete = existingChatLink.get();
        Link linkResponse = tgChatLinkToDelete.link(); // Получаем ссылку из связи
        chatLinkRepository.delete(tgChatLinkToDelete); // Удаляем связь
        log.info("Удалена связь между чатом {} и ссылкой {}", tgChatId, uri);

        // Проверка, остались ли другие связи с этой ссылкой
        if (chatLinkRepository.countByLinkId(linkResponse.id()) == 0) {
            // Если нет других связей, удаляем и саму ссылку
            linkRepository.delete(linkResponse);
            log.info("Ссылка {} удалена, так как больше не связана ни с одним чатом.", linkResponse.url());
        } else {
            log.info("Ссылка {} не удалена, так как связана с другими чатами.", linkResponse.url());
        }

        // Возвращаем ответ
        return mapper.LinkToLinkResponse(linkResponse);
    }


    // ----------------  Для scheduler
    @Transactional(readOnly = true)
    @Override
    public Optional<Link> findById(Long id) {
        return linkRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Link> getAllLinks(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return linkRepository.findAll(pageable).getContent();
    }

    @Transactional
    @Override
    public void update(Link link) {
        linkRepository.save(link);
    }

}
