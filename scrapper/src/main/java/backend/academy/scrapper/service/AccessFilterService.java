package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.request.filter.FilterRequest;
import backend.academy.scrapper.dto.response.filter.FilterListResponse;
import backend.academy.scrapper.dto.response.filter.FilterResponse;

public interface AccessFilterService {

    FilterResponse createFilter(Long id, FilterRequest filterRequest);

    FilterListResponse getAllFilter(Long tgChatId);

    FilterResponse deleteFilter(Long tgChatId, FilterRequest filterRequest);
}
