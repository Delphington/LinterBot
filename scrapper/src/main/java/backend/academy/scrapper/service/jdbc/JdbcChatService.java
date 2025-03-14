package backend.academy.scrapper.service.jdbc;

import backend.academy.scrapper.dao.chat.ChatDao;
import backend.academy.scrapper.entity.Chat;
import backend.academy.scrapper.exception.chat.ChatAlreadyExistsException;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class JdbcChatService implements ChatService {

    private final ChatDao chatDao;

    //Transaction

    @Override
    public void registerChat(Long id) {
        checkIsCorrect(id);
        if (chatDao.isExistChat(id)) {
            throw new ChatAlreadyExistsException("Чат уже существует с таким id = " + id);
        }
        chatDao.save(id);
        log.info("ChatService: Пользователь зарегистрирован id = {}", Utils.sanitize(id));
    }

    @Override
    public void deleteChat(Long id) {
        checkIsCorrect(id);

        if (!chatDao.isExistChat(id)) {
            throw new ChatAlreadyExistsException("Чат уже существует с таким id = " + id);
        }

        chatDao.remove(id);

        log.info("ChatService: Пользователь удален id = {}", Utils.sanitize(id));
    }

    @Override
    public Optional<Chat> findChatById(Long id) {
        return Optional.empty();
    }
}
