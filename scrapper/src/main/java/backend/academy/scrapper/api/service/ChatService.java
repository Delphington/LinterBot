package backend.academy.scrapper.api.service;

import backend.academy.scrapper.api.entity.TgChat;
import backend.academy.scrapper.api.exception.chat.ChatAlreadyExistsException;
import backend.academy.scrapper.api.exception.chat.ChatIllegalArgumentException;
import backend.academy.scrapper.api.exception.chat.ChatNotExistException;
import backend.academy.scrapper.api.repository.ChatRepository;
import backend.academy.scrapper.api.util.Utils;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatService {

    private final LinkService linkService;

    private final ChatRepository chatRepository;

    @Transactional
    public void registerChat(Long id) {
        checkIsCorrect(id);

        chatRepository.findById(id).ifPresent(tgChat -> {
            throw new ChatAlreadyExistsException("Чат уже существует с таким id = " + id);
        });

        chatRepository.save(new TgChat(id, OffsetDateTime.now()));

        log.info("ChatService: Пользователь зарегистрирован id = {}", Utils.sanitize(id));
        linkService.createAccount(id);
    }

    @Transactional
    public void deleteChat(Long id) {
        checkIsCorrect(id);

        chatRepository.findById(id).ifPresent(tgChat -> {
            throw new ChatNotExistException("Чата не существует с id = " + id);
        });

        chatRepository.deleteById(id);

        log.info("ChatService: Пользователь удален id = {}", Utils.sanitize(id));
    }

    private void checkIsCorrect(Long id) {
        if (id == null || id < 1) {
            throw new ChatIllegalArgumentException("Chat-id должно быть положительное, id = " + id);
        }
    }
}
