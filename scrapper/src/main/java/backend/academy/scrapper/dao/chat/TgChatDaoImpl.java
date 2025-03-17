package backend.academy.scrapper.dao.chat;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class TgChatDaoImpl implements TgChatDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String TABLE_NAME = "tg_chats";

    @Transactional(readOnly = true)
    @Override
    public boolean isExistChat(Long id) {
        String sql = "SELECT EXISTS (SELECT 1 FROM " + TABLE_NAME + " WHERE id = ?)";
        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        return result != null && result; // Возвращает false, если result == null
    }

    @Transactional
    @Override
    public void save(Long id) {
        OffsetDateTime now = OffsetDateTime.now(ZoneId.systemDefault());
        String sql = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?)";
        jdbcTemplate.update(sql, id, now);
    }

    @Transactional
    @Override
    public void remove(Long id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
