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

    private Long generatedLinkId = 1L;

    // ID - пользователя: Ссылка
    private Map<Long, List<LinkResponse>> repoLinks = new ConcurrentHashMap<>();

    // Сервис для отслеживания обновлений
    private final UpdateLinkService updateLinkService;


    //----------------------------------------------
    private final ChatService chatService;
    private final LinkRepository linkRepository;
    private final ChatLinkRepository chatLinkRepository;


    public void createAccount(Long tgChatId) {
        //linkRepository.sa
        repoLinks.put(tgChatId, new ArrayList<>());
    }

    // public record LinkResponse(Long id, URI url, List<String> tags, List<String> filters) {}


    @Transactional
    public ListLinksResponse getAllLinks(Long tgChatId) {
        if (!chatService.isExistChat(tgChatId)) {
            log.error("ОШИБКА ДОБАВЛЕНИЕ ССЫЛКИ, ТАКОГО ПОЛЬЗОВАТЕЛЯ НЕ СУЩЕСТВУЕТ");
            throw new ChatNotExistException("Чат с ID " + tgChatId + " не найден.");

        }


        log.info("LinkService: getAllLinks, id = {}", Utils.sanitize(tgChatId));
        List<Link> linkList = chatLinkRepository.findLinksByChatId(tgChatId);
        return new ListLinksResponse(maperLinkToReponseLinkList(linkList), linkList.size());
    }


    public List<LinkResponse> maperLinkToReponseLinkList(List<Link> linkList) {
        List<LinkResponse> list = new ArrayList<>();
        for(Link item : linkList) {
            LinkResponse lr =  new LinkResponse(item.id(), URI.create(item.url()), item.tags(), item.filters());
            list.add(lr);
        }
        return list;

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

        return new LinkResponse(newLink.id(), URI.create(newLink.url()), newLink.tags(), newLink.filters());
    }

    // Проверка существует ли вообще такой чат
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


        return new  LinkResponse(linkResponse.id(), URI.create(linkResponse.url()), linkResponse.tags(), linkResponse.filters());
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

    // -------------------------------------------------------------

    // проверяем uri по String, что uri в БД
    private Optional<LinkResponse> searchLinkByURI(List<LinkResponse> list, URI uri) {
        for (LinkResponse linkModel : list) {
            if (linkModel.url().toString().equals(uri.toString())) {
                return Optional.of(linkModel);
            }
        }
        return Optional.empty();
    }


}
