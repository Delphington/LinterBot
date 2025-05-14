package backend.academy.scrapper.service.orm;

import backend.academy.scrapper.dto.request.filter.FilterRequest;
import backend.academy.scrapper.dto.response.filter.FilterListResponse;
import backend.academy.scrapper.dto.response.filter.FilterResponse;
import backend.academy.scrapper.entity.AccessFilter;
import backend.academy.scrapper.entity.TgChat;
import backend.academy.scrapper.exception.chat.ChatNotExistException;
import backend.academy.scrapper.exception.filter.AccessFilterAlreadyExistException;
import backend.academy.scrapper.exception.filter.AccessFilterNotExistException;
import backend.academy.scrapper.mapper.FilterMapper;
import backend.academy.scrapper.repository.AccessFilterRepository;
import backend.academy.scrapper.repository.TgChatRepository;
import backend.academy.scrapper.service.AccessFilterService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrmAccessFilterService implements AccessFilterService {

    private final TgChatRepository tgChatRepository;
    private final AccessFilterRepository accessFilterRepository;
    private final FilterMapper filterMapper;

    @Override
    public FilterResponse createFilter(Long chatId, FilterRequest filterRequest) {
        log.info("Мы в OrmAccessFilterService createFilter");

        Optional<TgChat> tgChatOptional = tgChatRepository.findById(chatId);

        if (accessFilterRepository.existsAccessFilterByFilter(filterRequest.filter())) {
            log.info("Такой фильтр уже существует: {}", filterRequest.filter());
            throw new AccessFilterAlreadyExistException("Такая ссылка уже существует");
        }

        TgChat tgChat = tgChatOptional.orElseThrow(() -> new ChatNotExistException("Чата не существует"));

        AccessFilter accessFilter = accessFilterRepository.save(AccessFilter.create(tgChat, filterRequest.filter()));

        return filterMapper.toFilterResponse(accessFilter);
    }

    @Override
    public FilterListResponse getAllFilter(Long tgChatId) {
        Optional<TgChat> tgChatOptional = tgChatRepository.findById(tgChatId);

        TgChat tgChat = tgChatOptional.orElseThrow(() -> new ChatNotExistException("Чата не существует"));

        return new FilterListResponse(filterMapper.toFilterResponseList(tgChat.accessFilters()));
    }

    @Override
    public FilterResponse deleteFilter(Long tgChatId, FilterRequest filterRequest) {
        log.info("Мы в OrmAccessFilterService FilterResponse");

        Optional<TgChat> tgChatOptional = tgChatRepository.findById(tgChatId);

        TgChat tgChat = tgChatOptional.orElseThrow(() -> new ChatNotExistException("Чата не существует"));
        Optional<AccessFilter> optionalAccessFilter =
                deleteAccessFilter(tgChat.accessFilters(), filterRequest.filter());
        if (optionalAccessFilter.isEmpty()) {
            throw new AccessFilterNotExistException("Такого фильтра не существует!");
        }

        AccessFilter accessFilter =
                optionalAccessFilter.orElseThrow(() -> new AccessFilterNotExistException("Чата не существует"));

        tgChatRepository.save(tgChat);
        return new FilterResponse(accessFilter.id(), accessFilter.filter());
    }

    private Optional<AccessFilter> deleteAccessFilter(List<AccessFilter> accessFilterList, String filter) {
        for (AccessFilter item : accessFilterList) {
            if (item.filter().equals(filter)) {
                accessFilterList.remove(item);
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }
}
