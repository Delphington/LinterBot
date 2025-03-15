package backend.academy.scrapper.service;

import backend.academy.scrapper.entity.TgChat;
import backend.academy.scrapper.exception.chat.ChatIllegalArgumentException;
import java.util.Optional;

public interface ChatService {
    void registerChat(Long id);

    void deleteChat(Long id);

    Optional<TgChat> findChatById(Long id);

    default void checkIsCorrect(Long id) {
        if (id == null || id < 1) {
            throw new ChatIllegalArgumentException("Chat-id должно быть положительное, id = " + id);
        }
    }

    //  void saveChat(TgChat existingTgChat);
}
