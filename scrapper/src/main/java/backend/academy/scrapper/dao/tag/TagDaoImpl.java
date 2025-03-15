package backend.academy.scrapper.dao.tag;

import backend.academy.scrapper.dao.mapper.TagMapper;
import backend.academy.scrapper.entity.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TagDaoImpl implements TagDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String TABLE_TAGS = "tags";

    @Override
    public List<Tag> findListTagByLinkId(Long id) {
        String query = "SELECT id, tag, link_id FROM " + TABLE_TAGS + " WHERE link_id = ?";
        return jdbcTemplate.query(query, new Object[] {id}, new TagMapper());
    }

    @Override
    public void removeTag(Long id, String removedTag) {
        String query = "DELETE FROM " + TABLE_TAGS + " WHERE link_id = ? AND tag = ?";
        jdbcTemplate.update(query, new Object[] {id, removedTag});
    }
}
