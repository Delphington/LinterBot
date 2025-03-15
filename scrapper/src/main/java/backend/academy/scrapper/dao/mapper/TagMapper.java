package backend.academy.scrapper.dao.mapper;

import backend.academy.scrapper.entity.Tag;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TagMapper implements RowMapper<Tag> {

    @Override
    public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
        Tag tag = new Tag();
        tag.id(rs.getLong("id"));
        tag.tag(rs.getString("tag"));
        return tag;
    }
}
