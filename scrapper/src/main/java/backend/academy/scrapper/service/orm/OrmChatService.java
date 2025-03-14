package backend.academy.scrapper.service.orm;

import backend.academy.scrapper.entity.Chat;
import backend.academy.scrapper.exception.chat.ChatAlreadyExistsException;
import backend.academy.scrapper.exception.chat.ChatNotExistException;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.util.Utils;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Slf4j
public class OrmChatService implements ChatService {

    private final ChatRepository chatRepository;

    @Override
    @Transactional
    public void registerChat(Long id) {
        checkIsCorrect(id);

        chatRepository.findById(id).ifPresent(tgChat -> {
            throw new ChatAlreadyExistsException("Чат уже существует с таким id = " + id);
        });

        Chat chat = Chat.builder()
            .id(id)
            .createdAt(OffsetDateTime.now())
            .build();
        chatRepository.save(chat);

        log.info("ChatService: Пользователь зарегистрирован id = {}", Utils.sanitize(id));
    }

    @Override
    @Transactional
    public void deleteChat(Long id) {
        checkIsCorrect(id);

        chatRepository.findById(id).ifPresent(tgChat -> {
            throw new ChatNotExistException("Чата не существует с id = " + id);
        });

        chatRepository.deleteById(id);

        log.info("ChatService: Пользователь удален id = {}", Utils.sanitize(id));
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<Chat> findChatById(Long id) {
        return chatRepository.findById(id);
    }
}
