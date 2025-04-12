package backend.academy.scrapper.service.jdbc;

import backend.academy.scrapper.dao.accessfilter.AccessFilterDao;
import backend.academy.scrapper.dto.request.filter.FilterRequest;
import backend.academy.scrapper.dto.response.filter.FilterListResponse;
import backend.academy.scrapper.dto.response.filter.FilterResponse;
import backend.academy.scrapper.exception.filter.AccessFilterAlreadyExistException;
import backend.academy.scrapper.exception.filter.AccessFilterNotExistException;
import backend.academy.scrapper.service.AccessFilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JdbcAccessFilterService implements AccessFilterService {

    private final AccessFilterDao accessFilterDao;

    @Override
    public FilterResponse createFilter(Long id, FilterRequest filterRequest) {
        log.info("JdbcAccessFilterService Create filter");
        // Проверяем существование фильтра
        if (accessFilterDao.filterExists(filterRequest.filter())) {
            log.info("Такой фильтр уже существует: {}", filterRequest.filter());
            throw new AccessFilterAlreadyExistException("Такая ссылка уже существует");
        }
        FilterResponse createdFilter = accessFilterDao.createFilter(id, filterRequest);
        log.info("Фильтр создан");

        return createdFilter;
    }

    @Override
    public FilterListResponse getAllFilter(Long tgChatId) {
        log.info("JdbcAccessFilterService getAllFilter");
        return accessFilterDao.getAllFilter(tgChatId);
    }

    @Override
    public FilterResponse deleteFilter(Long tgChatId, FilterRequest filterRequest) {
        FilterResponse deletedFilter = accessFilterDao.deleteFilter(tgChatId, filterRequest);
        if (deletedFilter == null) {
            throw new AccessFilterNotExistException("Такого фильтра не существует!");
        }

        return deletedFilter;
    }
}
