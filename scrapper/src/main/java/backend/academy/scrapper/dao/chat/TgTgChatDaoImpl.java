package backend.academy.scrapper.dao.chat;

import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TgTgChatDaoImpl implements TgChatDao {

    private final JdbcTemplate jdbcTemplate;

    private final static String TABLE_NAME = "tg_chats";

    @Override
    public boolean isExistChat(Long id) {
        String sql = "SELECT EXISTS (SELECT 1 FROM " + TABLE_NAME + " WHERE id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    @Override
    public void save(Long id) {
        OffsetDateTime now = OffsetDateTime.now();
        String sql = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?)";
        jdbcTemplate.update(sql, id, now);
    }

    @Override
    public void remove(Long id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
