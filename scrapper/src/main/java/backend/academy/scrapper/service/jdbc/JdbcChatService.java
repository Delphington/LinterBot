package backend.academy.scrapper.service.jdbc;

import backend.academy.scrapper.dao.chat.TgChatDao;
import backend.academy.scrapper.entity.TgChat;
import backend.academy.scrapper.exception.chat.ChatAlreadyExistsException;
import backend.academy.scrapper.exception.chat.ChatNotExistException;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.util.Utils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JdbcChatService implements ChatService {

    private final TgChatDao tgChatDao;

    @Override
    public void registerChat(Long id) {
        checkIsCorrect(id);
        if (tgChatDao.isExistChat(id)) {
            throw new ChatAlreadyExistsException("Чат уже существует с таким chatId = " + id);
        }
        tgChatDao.save(id);
        log.info("ChatService: Пользователь зарегистрирован chatId = {}", Utils.sanitize(id));
    }

    @Override
    public void deleteChat(Long id) {
        checkIsCorrect(id);

        if (!tgChatDao.isExistChat(id)) {
            throw new ChatNotExistException("Чат не существует с таким chatId = " + id);
        }

        tgChatDao.remove(id);

        log.info("ChatService: Пользователь удален chatId = {}", Utils.sanitize(id));
    }

    @Override
    public Optional<TgChat> findChatById(Long id) {
        return Optional.empty();
    }
}
