package backend.academy.scrapper.service.orm;

import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.entity.Chat;
import backend.academy.scrapper.entity.ChatLink;
import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.exception.chat.ChatNotExistException;
import backend.academy.scrapper.exception.link.LinkAlreadyExistException;
import backend.academy.scrapper.exception.link.LinkNotFoundException;
import backend.academy.scrapper.mapper.LinkMapper;
import backend.academy.scrapper.repository.ChatLinkRepository;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.util.Utils;
import java.net.URI;
import java.util.List;
import java.util.Optional;
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

    private final LinkRepository linkRepository;
    private final ChatLinkRepository chatLinkRepository;
    private final LinkMapper mapper;
    private final ChatService chatService;

    @Transactional(readOnly = true)
    @Override
    public ListLinksResponse getAllLinks(Long tgChatId) {

        Optional<Chat> chatOptional = chatService.findChatById(tgChatId);

        if (chatOptional.isEmpty()) {
            log.error("Ошибка, пользователя не существует");
            throw new ChatNotExistException("Чат с ID " + tgChatId + " не найден.");
        }

        log.info("LinkService: getAllLinks, id = {}", Utils.sanitize(tgChatId));
        List<Link> linkList = chatLinkRepository.findLinksByChatId(tgChatId);
        return new ListLinksResponse(mapper.LinkListToLinkResponseList(linkList), linkList.size());
    }


    @Transactional
    @Override
    public LinkResponse addLink(Long tgChatId, AddLinkRequest request) {
        Optional<Chat> chatOptional = chatService.findChatById(tgChatId);

        if (chatOptional.isEmpty()) {
            log.error("Ошибка, пользователя не существует");
            throw new ChatNotExistException("Чат с ID " + tgChatId + " не найден.");
        }

        // Проверяем, существует ли ссылка именно для этого tgChatId
        Optional<Link> existingLink = chatLinkRepository.findLinkByChatIdAndUrl(tgChatId, request.link().toString());
        if (existingLink.isPresent()) {
            throw new LinkAlreadyExistException("Такая ссылка уже существует для этого чата");
        }

        Chat existingChat = chatOptional.get();

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
    @Override
    public LinkResponse deleteLink(Long tgChatId, URI uri) {
        Optional<Chat> chatOptional = chatService.findChatById(tgChatId);

        if (chatOptional.isEmpty()) {
            log.error("Ошибка, пользователя не существует");
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
