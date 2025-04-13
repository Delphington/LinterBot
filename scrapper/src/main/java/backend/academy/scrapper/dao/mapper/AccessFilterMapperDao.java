package backend.academy.scrapper.dao.mapper;

import backend.academy.scrapper.dto.response.filter.FilterResponse;
import backend.academy.scrapper.entity.AccessFilter;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccessFilterMapperDao implements RowMapper<AccessFilter> {
    @Override
    public AccessFilter mapRow(ResultSet rs, int rowNum) throws SQLException {
        return AccessFilter.builder()
            .id(rs.getLong("id"))
            .filter(rs.getString("filter"))
            .build();
    }

    public static FilterResponse toResponse(AccessFilter accessFilter) {
        return new FilterResponse(
            accessFilter.id(),
            accessFilter.filter()
        );
    }
}
