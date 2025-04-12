package backend.academy.scrapper.mapper;

import backend.academy.scrapper.dto.response.filter.FilterResponse;
import backend.academy.scrapper.entity.AccessFilter;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class FilterMapper {

    public FilterResponse toFilterResponse(AccessFilter accessFilter) {
        if (accessFilter == null) {
            return null;
        }
        return new FilterResponse(accessFilter.id(), accessFilter.filter());
    }

    public List<FilterResponse> toFilterResponseList(List<AccessFilter> accessFilters) {
        if (accessFilters == null) {
            return Collections.emptyList();
        }
        return accessFilters.stream()
            .map(this::toFilterResponse)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
