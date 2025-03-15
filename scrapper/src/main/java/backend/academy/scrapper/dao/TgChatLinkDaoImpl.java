package backend.academy.scrapper.dao;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class TgChatLinkDaoImpl implements TgChatLinkDao {

    private final JdbcTemplate jdbcTemplate;
    private static final String TABLE_NAME = "tg_chat_links";

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
        jdbcTemplate.update(QUERY, chatId, linkId);
    }
}
