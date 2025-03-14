package backend.academy.scrapper.dao.mapper;

import backend.academy.scrapper.entity.Link;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class MapperLinkDao implements RowMapper<Link> {


    @Override
    public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
//        Link link = new Link();
//        link.id(rs.getLong("id"));
//        link.url(rs.getString("url"));
//        link.tags(convertArrayToList(rs.getArray("tags")));
//        link.filters(convertArrayToList(rs.getArray("filters")));
//        link.description(rs.getString("description"));
//        link.updatedAt(rs.getObject("updated_at", OffsetDateTime.class));
        return null;

    }
    private List<String> convertArrayToList(java.sql.Array array) throws SQLException {
        if (array == null) {
            return new ArrayList<>();
        }
        return List.of((String[]) array.getArray());
    }
}
