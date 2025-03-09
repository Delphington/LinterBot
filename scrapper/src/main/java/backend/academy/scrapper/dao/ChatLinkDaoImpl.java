package backend.academy.scrapper.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ChatLinkDaoImpl implements ChatLinkDao {

    private final JdbcTemplate jdbcTemplate;
    private final static String TABLE_NAME = "tg_chat_link";

    @Override
    public List<Long> getLinkIdsByChatId(Long chatId) {
        String QUERY = "SELECT link_id FROM " + TABLE_NAME + " WHERE tg_chat_id = ?";
        List<Long> linkIds = jdbcTemplate.queryForList(QUERY, Long.class, chatId);
        return linkIds;
    }

    @Override
    public void addRecord(Long chatId, Long linkId) {
        log.info("Добавление записи в ChatLink: chatId={}, linkId={}", chatId, linkId);

        String QUERY = "INSERT INTO " + TABLE_NAME + " (tg_chat_id, link_id) VALUES (?, ?)"; // Укажите имена столбцов
        try {
            int rowsAffected = jdbcTemplate.update(QUERY, chatId, linkId);

            if (rowsAffected > 0) {
                log.info("Успешно добавлена запись в таблицу {} с chatId={} и linkId={}", TABLE_NAME, chatId, linkId);
            } else {
                log.warn("Не удалось добавить запись в таблицу {} с chatId={} и linkId={}.  Возможно, запись уже существует или возникла другая проблема.", TABLE_NAME, chatId, linkId);
            }

            System.err.println("Запись добавлена в ChatLink"); // Оставьте эту строку временно, пока не убедитесь, что логи работают

        } catch (Exception e) {
            log.error("Ошибка при добавлении записи в таблицу {} с chatId={} и linkId={}: {}", TABLE_NAME, chatId, linkId, e.getMessage(), e);
            throw e; // Перебросьте исключение, чтобы вызывающий код мог обработать его
        }
    }
}
