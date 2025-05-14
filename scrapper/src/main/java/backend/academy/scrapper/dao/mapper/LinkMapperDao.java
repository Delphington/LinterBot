package backend.academy.scrapper.dao.mapper;

import backend.academy.scrapper.entity.Link;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.jdbc.core.RowMapper;

public class LinkMapperDao implements RowMapper<Link> {

    @Override
    public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Link.builder()
                .id(rs.getLong("id"))
                .url(rs.getString("url"))
                .description(rs.getString("description"))
                .updatedAt(mapToOffsetDateTime(rs.getTimestamp("updated_at")))
                .build();
    }

    private OffsetDateTime mapToOffsetDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toInstant().atOffset(ZoneOffset.UTC) : null;
    }
}
