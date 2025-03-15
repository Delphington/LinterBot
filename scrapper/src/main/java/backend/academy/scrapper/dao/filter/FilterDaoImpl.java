package backend.academy.scrapper.dao.filter;

import backend.academy.scrapper.dao.mapper.FilterMapper;
import backend.academy.scrapper.entity.Filter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FilterDaoImpl implements FilterDao {

    private final JdbcTemplate jdbcTemplate;

    private final static String TABLE_FILTERS = "filters";

    @Override
    public List<Filter> findListFilterByLinkId(Long id) {

        // SQL-запрос для получения тегов по link_id
        String query = "SELECT id, filter, link_id FROM " + TABLE_FILTERS + " WHERE link_id = ?";

        // Используем TagMapper для преобразования результата
        return jdbcTemplate.query(query, new Object[]{id}, new FilterMapper());
    }
}
