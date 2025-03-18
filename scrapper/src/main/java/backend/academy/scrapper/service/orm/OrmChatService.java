package backend.academy.scrapper.service.orm;

import backend.academy.scrapper.entity.TgChat;
import backend.academy.scrapper.exception.chat.ChatAlreadyExistsException;
import backend.academy.scrapper.exception.chat.ChatNotExistException;
import backend.academy.scrapper.repository.TgChatRepository;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.util.Utils;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public class OrmChatService implements ChatService {

    private final TgChatRepository tgChatRepository;

    @Override
    @Transactional
    public void registerChat(Long id) {
        checkIsCorrect(id);

        tgChatRepository.findById(id).ifPresent(tgChat -> {
            throw new ChatAlreadyExistsException("Чат уже существует с таким id = " + id);
        });

        TgChat tgChat = TgChat.builder()
                .id(id)
                .createdAt(OffsetDateTime.now(ZoneId.systemDefault()))
                .build();
        tgChatRepository.save(tgChat);

        log.info("ChatService: Пользователь зарегистрирован id = {}", Utils.sanitize(id));
    }

    @Override
    @Transactional
    public void deleteChat(Long id) {
        checkIsCorrect(id);

        tgChatRepository.findById(id).ifPresent(tgChat -> {
            throw new ChatNotExistException("Чата не существует с id = " + id);
        });

        tgChatRepository.deleteById(id);

        log.info("ChatService: Пользователь удален id = {}", Utils.sanitize(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TgChat> findChatById(Long id) {
        return tgChatRepository.findById(id);
    }
}
