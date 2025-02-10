package backend.academy.scrapper.api.service;

import backend.academy.scrapper.api.exception.ChatAlreadyExistsException;
import backend.academy.scrapper.api.exception.ChatNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    //id или код чата либо ссылка
    private Map<Long, Long> chats = new ConcurrentHashMap<>();


    public void registerChat(Long id) {
        if (isExistChat(id)) {
            throw new ChatAlreadyExistsException("Чат уже существует с таким id = " + id);
        }
        chats.put(id, id); //todo: продумать что перехранить в мапе
    }

    public void deleteChat(Long id) {
        if (!isExistChat(id)) {
            throw new ChatNotFoundException("Чата не существует с id = " + id);
        }
        chats.remove(id);
    }


    private boolean isExistChat(Long id) {
        return chats.containsKey(id);
    }

}
