package backend.academy.scrapper.dao.mapper;

import backend.academy.scrapper.entity.Filter;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FilterMapper implements RowMapper<Filter> {
    @Override
    public Filter mapRow(ResultSet rs, int rowNum) throws SQLException {
        Filter filter = new Filter();
        filter.id(rs.getLong("id"));
        filter.filter(rs.getString("filter"));
        return filter;
    }
}
