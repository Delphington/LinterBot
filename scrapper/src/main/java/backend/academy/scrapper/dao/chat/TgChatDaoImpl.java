package backend.academy.scrapper.dao.chat;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class TgChatDaoImpl implements TgChatDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String EXISTS_QUERY = "SELECT 1 FROM tg_chats WHERE id = ? LIMIT 1";
    private static final String INSERT_QUERY = "INSERT INTO tg_chats VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM tg_chats WHERE id = ?";


    @Transactional(readOnly = true)
    @Override
    public boolean isExistChat(Long id) {
        try {
            Integer result = jdbcTemplate.queryForObject(EXISTS_QUERY, Integer.class, id);
            return result != null;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Transactional
    @Override
    public void save(Long id) {
        OffsetDateTime now = OffsetDateTime.now(ZoneId.systemDefault());
        jdbcTemplate.update(INSERT_QUERY, id, now);
    }

    @Transactional
    @Override
    public void remove(Long id) {
        jdbcTemplate.update(DELETE_QUERY, id);
    }
}
