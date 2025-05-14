package backend.academy.bot.client.filter;

import backend.academy.bot.api.dto.request.filter.FilterRequest;
import backend.academy.bot.api.dto.response.filter.FilterListResponse;
import backend.academy.bot.api.dto.response.filter.FilterResponse;

public interface ScrapperFilterClient {

    FilterResponse createFilter(Long chatId, FilterRequest filterRequest);

    FilterResponse deleteFilter(Long tgChatId, FilterRequest filterRequest);

    FilterListResponse getFilterList(Long id);
}
