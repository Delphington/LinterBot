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

    private static final String GET_LINK_IDS_QUERY =
        "SELECT link_id FROM tg_chat_links WHERE tg_chat_id = ?";
    private static final String ADD_RECORD_QUERY =
        "INSERT INTO tg_chat_links (tg_chat_id, link_id) VALUES (?, ?)";

    @Override
    public List<Long> getLinkIdsByChatId(Long chatId) {
        return jdbcTemplate.queryForList(GET_LINK_IDS_QUERY, Long.class, chatId);
    }

    @Override
    public void addRecord(Long chatId, Long linkId) {
        log.info("Добавление записи в ChatLink: chatId={}, linkId={}", chatId, linkId);
        jdbcTemplate.update(ADD_RECORD_QUERY, chatId, linkId);
    }
}
