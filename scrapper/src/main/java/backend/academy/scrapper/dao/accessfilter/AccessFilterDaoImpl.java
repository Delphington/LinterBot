package backend.academy.scrapper.dao.accessfilter;

import backend.academy.scrapper.dto.request.filter.FilterRequest;
import backend.academy.scrapper.dto.response.filter.FilterListResponse;
import backend.academy.scrapper.dto.response.filter.FilterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class AccessFilterDaoImpl implements AccessFilterDao {

    private final JdbcTemplate jdbcTemplate;
    private final String ACCESS_FILTER_TABLE = "access_filter";

    @Override
    public boolean filterExists(String filter) {
        String sql = "SELECT COUNT(*) FROM " + ACCESS_FILTER_TABLE + " WHERE filter = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filter);
        return count != null && count > 0;
    }

    @Override
    public FilterResponse createFilter(Long id, FilterRequest filterRequest) {
        log.info("AccessFilterDaoImpl Creating Access Filter");

        String sql = "INSERT INTO " + ACCESS_FILTER_TABLE + " (tg_chat_id, filter) VALUES (?, ?) RETURNING id, filter";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                new FilterResponse(rs.getLong("id"), rs.getString("filter")),
            id, filterRequest.filter()
        );
    }

    @Override
    public FilterListResponse getAllFilter(Long tgChatId) {
        log.info("AccessFilterDaoImpl getAllFilter");
        String sql = "SELECT id, filter FROM " + ACCESS_FILTER_TABLE + " WHERE tg_chat_id = ?";

        List<FilterResponse> filters = jdbcTemplate.query(sql, (rs, rowNum) ->
                new FilterResponse(rs.getLong("id"), rs.getString("filter")),
            tgChatId
        );
        return new FilterListResponse(filters);
    }

    @Override
    public FilterResponse deleteFilter(Long tgChatId, FilterRequest filterRequest) {
        log.info("AccessFilterDaoImpl deleteFilter");
        // Сначала проверяем существование фильтра для данного чата
        String checkSql = "SELECT id FROM " + ACCESS_FILTER_TABLE + " WHERE tg_chat_id = ? AND filter = ?";
        List<Long> ids = jdbcTemplate.query(checkSql, (rs, rowNum) -> rs.getLong("id"), tgChatId, filterRequest.filter());

        if (ids.isEmpty()) {
            return null;
        }

        Long filterId = ids.get(0);

        // Удаляем фильтр
        String deleteSql = "DELETE FROM " + ACCESS_FILTER_TABLE + " WHERE id = ? RETURNING id, filter";
        return jdbcTemplate.queryForObject(deleteSql, (rs, rowNum) ->
                new FilterResponse(rs.getLong("id"), rs.getString("filter")),
            filterId
        );
    }
}
