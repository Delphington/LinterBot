package backend.academy.scrapper.api.service;

import backend.academy.scrapper.api.exception.chat.ChatAlreadyExistsException;
import backend.academy.scrapper.api.exception.chat.ChatIllegalArgumentException;
import backend.academy.scrapper.api.exception.chat.ChatNotExistException;
import backend.academy.scrapper.client.tracker.UpdateLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Log4j2
@Service
public class ChatService {

    //todo: id или код чата либо ссылка
    private Map<Long, Long> chats = new ConcurrentHashMap<>();

    private final LinkService linkService;

    public void registerChat(Long id) {
        checkIsCorrect(id);

        if (isExistChat(id)) {
            throw new ChatAlreadyExistsException("Чат уже существует с таким id = " + id);
        }
        chats.put(id, id);

        log.info("==ChatService: Пользователь зарегистрирован id = {}", id);
        linkService.createAccount(id);
    }

    public void deleteChat(Long id) {
        checkIsCorrect(id);

        if (!isExistChat(id)) {
            throw new ChatNotExistException("Чата не существует с id = " + id);
        }
        chats.remove(id);
        log.info("==ChatService: Пользователь удален id = {}", id);
    }


    private void checkIsCorrect(Long id) {
        if (id == null || id < 1) {
            throw new ChatIllegalArgumentException("Chat-id должно быть положительное, id = " + id);
        }
    }

    private boolean isExistChat(Long id) {
        return chats.containsKey(id);
    }

}
