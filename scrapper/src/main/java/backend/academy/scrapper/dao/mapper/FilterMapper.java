package backend.academy.scrapper.dao.mapper;

import backend.academy.scrapper.entity.Filter;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class FilterMapper implements RowMapper<Filter> {
    @Override
    public Filter mapRow(ResultSet rs, int rowNum) throws SQLException {
        Filter filter = new Filter();
        filter.id(rs.getLong("id"));
        filter.filter(rs.getString("filter"));
        return filter;
    }
}
