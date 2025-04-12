package backend.academy.scrapper.dao.accessfilter;

import backend.academy.scrapper.dto.request.filter.FilterRequest;
import backend.academy.scrapper.dto.response.filter.FilterListResponse;
import backend.academy.scrapper.dto.response.filter.FilterResponse;

public interface AccessFilterDao {

    boolean filterExists(String filter);

    FilterResponse createFilter(Long id, FilterRequest filterRequest);

    FilterListResponse getAllFilter(Long tgChatId);

    FilterResponse deleteFilter(Long tgChatId, FilterRequest filterRequest);
}
