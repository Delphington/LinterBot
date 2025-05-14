package backend.academy.scrapper.dao.filter;

import backend.academy.scrapper.dao.mapper.FilterMapperDao;
import backend.academy.scrapper.entity.Filter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class FilterDaoImpl implements FilterDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_FILTERS_QUERY = "SELECT id, filter, link_id FROM filters WHERE link_id = ?";

    @Transactional(readOnly = true)
    @Override
    public List<Filter> findListFilterByLinkId(Long id) {
        return jdbcTemplate.query(FIND_FILTERS_QUERY, new Object[] {id}, new FilterMapperDao());
    }
}
