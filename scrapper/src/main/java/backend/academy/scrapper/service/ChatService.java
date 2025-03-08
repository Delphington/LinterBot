package backend.academy.scrapper.service;

import backend.academy.scrapper.entity.Chat;
import backend.academy.scrapper.exception.chat.ChatIllegalArgumentException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public interface ChatService {
    void registerChat(Long id);

    void deleteChat(Long id);

    Optional<Chat> findChatById(Long id);


    default void checkIsCorrect(Long id) {
        if (id == null || id < 1) {
            throw new ChatIllegalArgumentException("Chat-id должно быть положительное, id = " + id);
        }
    }
}
