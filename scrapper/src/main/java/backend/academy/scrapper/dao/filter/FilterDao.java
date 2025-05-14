package backend.academy.scrapper.dao.filter;

import backend.academy.scrapper.entity.Filter;
import java.util.List;

public interface FilterDao {
    List<Filter> findListFilterByLinkId(Long id);
}
