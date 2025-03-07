package backend.academy.scrapper.api.service;

import backend.academy.scrapper.api.dto.request.AddLinkRequest;
import backend.academy.scrapper.api.dto.response.LinkResponse;
import backend.academy.scrapper.api.dto.response.ListLinksResponse;
import backend.academy.scrapper.api.entity.Chat;
import backend.academy.scrapper.api.entity.ChatLink;
import backend.academy.scrapper.api.entity.Link;
import backend.academy.scrapper.api.exception.chat.ChatNotExistException;
import backend.academy.scrapper.api.exception.link.LinkAlreadyExistException;
import backend.academy.scrapper.api.exception.link.LinkNotFoundException;
import backend.academy.scrapper.api.mapper.LinkMapper;
import backend.academy.scrapper.api.repository.ChatLinkRepository;
import backend.academy.scrapper.api.repository.ChatRepository;
import backend.academy.scrapper.api.repository.LinkRepository;
import backend.academy.scrapper.api.util.Utils;
import backend.academy.scrapper.tracker.update.service.UpdateLinkService;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class LinkService {

    // todo: проверка, что взаимодействие начинается с /start

    private final LinkMapper mapper;

    //----------------------------------------------
    private final ChatService chatService;
    private final LinkRepository linkRepository;
    private final ChatLinkRepository chatLinkRepository;


    @Transactional(readOnly = true)
    public ListLinksResponse getAllLinks(Long tgChatId) {
        if (!chatService.isExistChat(tgChatId)) {
            log.error("ОШИБКА ДОБАВЛЕНИЕ ССЫЛКИ, ТАКОГО ПОЛЬЗОВАТЕЛЯ НЕ СУЩЕСТВУЕТ");
            throw new ChatNotExistException("Чат с ID " + tgChatId + " не найден.");
        }

        log.info("LinkService: getAllLinks, id = {}", Utils.sanitize(tgChatId));
        List<Link> linkList = chatLinkRepository.findLinksByChatId(tgChatId);
        return new ListLinksResponse(mapper.LinkListToLinkResponseList(linkList), linkList.size());
    }


    @Transactional
    public LinkResponse addLink(Long tgChatId, AddLinkRequest request) {
        if (!chatService.isExistChat(tgChatId)) {
            log.error("ОШИБКА ДОБАВЛЕНИЕ ССЫЛКИ, ТАКОГО ПОЛЬЗОВАТЕЛЯ НЕ СУЩЕСТВУЕТ");
            throw new ChatNotExistException("Чат с ID " + tgChatId + " не найден.");
        }

        // Проверяем, существует ли ссылка именно для этого tgChatId
        Optional<Link> existingLink = chatLinkRepository.findLinkByChatIdAndUrl(tgChatId, request.link().toString());
        if (existingLink.isPresent()) {
            throw new LinkAlreadyExistException("Такая ссылка уже существует для этого чата");
        }

        Chat existingChat = chatService.findChatById(tgChatId).get();

        Link newLink = new Link();
        newLink.url(request.link().toString());
        newLink.tags(request.tags());
        newLink.filters(request.filters());


        // Сохраняем ссылку в базе данных
        Link savedLink = linkRepository.save(newLink);

        // связь между чатом и ссылкой
        ChatLink chatLink = new ChatLink();
        chatLink.setChat(existingChat); // Устанавливаем существующий чат
        chatLink.setLink(savedLink);    // Устанавливаем новую ссылку
        chatLinkRepository.save(chatLink);

        // Обновляем список chatLinks в существующем чате
        existingChat.chatLinks().add(chatLink);

        return mapper.LinkToLinkResponse(savedLink);
    }

    @Transactional
    public LinkResponse deleteLink(Long tgChatId, URI uri) {
        if (!chatService.isExistChat(tgChatId)) {
            log.error("ОШИБКА ДОБАВЛЕНИЕ ССЫЛКИ, ТАКОГО ПОЛЬЗОВАТЕЛЯ НЕ СУЩЕСТВУЕТ");
            throw new ChatNotExistException("Чат с ID " + tgChatId + " не найден.");
        }

        // Проверка существования связи между чатом и ссылкой
        Optional<ChatLink> existingChatLink = chatLinkRepository.findByChatIdAndLinkUrl(tgChatId, uri.toString());
        if (existingChatLink.isEmpty()) {
            log.warn("Ссылка {} не найдена в чате {}", uri, tgChatId);
            throw new LinkNotFoundException("Ссылка " + uri + " не найдена в чате с ID " + tgChatId + ".");
        }

        // Удаление связи между чатом и ссылкой
        ChatLink chatLinkToDelete = existingChatLink.get();
        Link linkResponse = chatLinkToDelete.link();
        chatLinkRepository.delete(chatLinkToDelete);
        log.info("Удалена связь между чатом {} и ссылкой {}", tgChatId, uri);
        // Проверка, остались ли другие связи с этой ссылкой
        if (chatLinkRepository.countByLinkId(linkResponse.id()) == 0) {
            // Если нет других связей, удаляем и саму ссылку
            linkRepository.delete(linkResponse);
            log.info("Ссылка {} удалена, так как больше не связана ни с одним чатом.", linkResponse.url());
        } else {
            log.info("Ссылка {} не удалена, так как связана с другими чатами.", linkResponse.url());
        }

        return mapper.LinkToLinkResponse(linkResponse);
    }

    public Optional<Link> findById(Long id) {
        return linkRepository.findById(id);
    }


    public List<Link> getAllLinks() {
        return linkRepository.findAll();
    }

    public void save(Link link) {
        linkRepository.save(link);
    }
}
