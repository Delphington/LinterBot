package backend.academy.scrapper.dao.accessfilter;

import backend.academy.scrapper.dao.mapper.AccessFilterMapperDao;
import backend.academy.scrapper.dto.request.filter.FilterRequest;
import backend.academy.scrapper.dto.response.filter.FilterListResponse;
import backend.academy.scrapper.dto.response.filter.FilterResponse;
import backend.academy.scrapper.entity.AccessFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Objects;

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
        return AccessFilterMapperDao.toResponse(Objects.requireNonNull(jdbcTemplate.queryForObject(
            sql, new AccessFilterMapperDao(), id, filterRequest.filter())));
    }

    @Override
    public FilterListResponse getAllFilter(Long tgChatId) {
        log.info("AccessFilterDaoImpl getAllFilter");
        String sql = "SELECT id, filter FROM " + ACCESS_FILTER_TABLE + " WHERE tg_chat_id = ?";

        List<AccessFilter> filters = jdbcTemplate.query(sql, new AccessFilterMapperDao(), tgChatId);
        return new FilterListResponse(
            filters.stream()
                .map(AccessFilterMapperDao::toResponse)
                .toList()
        );
    }

    @Override
    public FilterResponse deleteFilter(Long tgChatId, FilterRequest filterRequest) {
        log.info("Deleting filter for chatId: {}", tgChatId);

        String findSql = "SELECT * FROM " + ACCESS_FILTER_TABLE +
                         " WHERE tg_chat_id = ? AND filter = ?";

        List<AccessFilter> filters = jdbcTemplate.query(findSql,
            new AccessFilterMapperDao(), tgChatId, filterRequest.filter());

        if (filters.isEmpty()) {
            return null;
        }

        String deleteSql = "DELETE FROM " + ACCESS_FILTER_TABLE +
                           " WHERE id = ? RETURNING *";

        AccessFilter deletedFilter = jdbcTemplate.queryForObject(deleteSql,
            new AccessFilterMapperDao(), filters.get(0).id());

        return AccessFilterMapperDao.toResponse(deletedFilter);
    }
}
