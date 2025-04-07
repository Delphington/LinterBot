package backend.academy.scrapper.dao.link;

import backend.academy.scrapper.dao.mapper.FilterMapperDao;
import backend.academy.scrapper.dao.mapper.LinkMapperDao;
import backend.academy.scrapper.dao.mapper.TagMapperDao;
import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.entity.Filter;
import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.entity.Tag;
import backend.academy.scrapper.exception.chat.ChatNotExistException;
import backend.academy.scrapper.exception.link.LinkNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class LinkDaoImpl implements LinkDao {

    private final JdbcTemplate jdbcTemplate;
    private static final String TABLE_LINKS = "links";
    private static final String TABLE_FILTERS = "filters";
    private static final String TABLE_TAGS = "tags";

    @Transactional(readOnly = true)
    @Override
    public List<Link> getListLinksByListLinkId(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", ids);

        String linksSql = "SELECT id, url, description, updated_at FROM " + TABLE_LINKS + " WHERE id IN (:ids)";
        List<Link> links = namedTemplate.query(linksSql, parameters, new LinkMapperDao());

        Set<Long> foundIds = links.stream().map(Link::id).collect(Collectors.toSet());
        for (Long id : ids) {
            if (!foundIds.contains(id)) {
                throw new LinkNotFoundException("Такой ссылки нет: " + id);
            }
        }

        String allTagsSql = "SELECT link_id, id, tag FROM " + TABLE_TAGS + " WHERE link_id IN (:ids)";
        List<Map<String, Object>> allTags = namedTemplate.queryForList(allTagsSql, parameters);

        String allFiltersSql = "SELECT link_id, id, filter FROM " + TABLE_FILTERS + " WHERE link_id IN (:ids)";
        List<Map<String, Object>> allFilters = namedTemplate.queryForList(allFiltersSql, parameters);

        Map<Long, List<Tag>> tagsByLinkId = new HashMap<>();
        Map<Long, List<Filter>> filtersByLinkId = new HashMap<>();

        for (Map<String, Object> tagRow : allTags) {
            Long linkId = (Long) tagRow.get("link_id");
            Long tagId = (Long) tagRow.get("id");
            String tagName = (String) tagRow.get("tag");

            Tag tag = Tag.create(tagId, tagName);
            tagsByLinkId.computeIfAbsent(linkId, k -> new ArrayList<>()).add(tag);
        }

        for (Map<String, Object> filterRow : allFilters) {
            Long linkId = (Long) filterRow.get("link_id");
            Long filterId = (Long) filterRow.get("id");
            String filterName = (String) filterRow.get("filter");

            Filter filter = Filter.create(filterId, filterName);
            filtersByLinkId.computeIfAbsent(linkId, k -> new ArrayList<>()).add(filter);
        }

        for (Link link : links) {
            Long linkId = link.id();
            link.tags(tagsByLinkId.getOrDefault(linkId, Collections.emptyList()));
            link.filters(filtersByLinkId.getOrDefault(linkId, Collections.emptyList()));
        }

        return links;
    }

    @Transactional
    @Override
    public Long addLink(AddLinkRequest request) {
        log.debug("Начало добавления ссылки: {}", request.link());
        // Вставка ссылки с одновременным получением ID
        Long linkId = jdbcTemplate.queryForObject(
                "INSERT INTO " + TABLE_LINKS + " (url, description, updated_at) VALUES (?, ?, ?) RETURNING id",
                Long.class,
                request.link().toString(),
                null,
                null);

        if (linkId == null) {
            throw new ChatNotExistException("Не удалось получить ID вставленной записи");
        }

        // Вставка тегов
        if (request.tags() != null && !request.tags().isEmpty()) {
            String insertTagSql = "INSERT INTO " + TABLE_TAGS + " (link_id, tag) VALUES (?, ?)";
            for (String tag : request.tags()) {
                jdbcTemplate.update(insertTagSql, linkId, tag);
            }
            log.info("Теги вставлены в таблицу tags для ссылки с id = {}", linkId);
        }

        // Вставка фильтров
        if (request.filters() != null && !request.filters().isEmpty()) {
            String insertFilterSql = "INSERT INTO " + TABLE_FILTERS + " (link_id, filter) VALUES (?, ?)";
            for (String filter : request.filters()) {
                jdbcTemplate.update(insertFilterSql, linkId, filter);
            }
            log.info("Фильтры вставлены в таблицу filters для ссылки с id = {}", linkId);
        }

        return linkId;
    }

    @Transactional
    @Override
    public void remove(Long id) {
        log.info("Удаление записи из таблицы {} с ID: {}", TABLE_LINKS, id);
        String sql = "DELETE FROM " + TABLE_LINKS + " WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Link> findLinkByLinkId(Long id) {
        // Запрос для получения данных о ссылке
        String linkSql = "SELECT id, url, description, updated_at FROM " + TABLE_LINKS + " WHERE id = ?";

        Optional<Link> linkOptional =
                jdbcTemplate.query(linkSql, new LinkMapperDao(), id).stream().findFirst();

        if (linkOptional.isEmpty()) {
            return Optional.empty();
        }

        Link link = linkOptional.orElseThrow(() -> new LinkNotFoundException("Link not found"));

        String tagsSql = "SELECT id, tag FROM " + TABLE_TAGS + " WHERE link_id = ?";
        List<Tag> tags = jdbcTemplate.query(tagsSql, new TagMapperDao(), id);
        link.tags(tags);

        String filtersSql = "SELECT id, filter FROM " + TABLE_FILTERS + " WHERE link_id = ?";
        List<Filter> filters = jdbcTemplate.query(filtersSql, new FilterMapperDao(), id);
        link.filters(filters);

        return Optional.of(link);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Link> getAllLinks(int offset, int limit) {
        // Запрос для получения данных о ссылках
        String linksSql = "SELECT id, url, description, updated_at FROM links LIMIT ? OFFSET ?";

        List<Link> links = jdbcTemplate.query(linksSql, new Object[] {limit, offset}, new LinkMapperDao());

        // Для каждой ссылки получаем теги и фильтры
        for (Link link : links) {
            Long linkId = link.id();

            String tagsSql = "SELECT id, tag FROM tags WHERE link_id = ?";
            List<Tag> tags = jdbcTemplate.query(tagsSql, new TagMapperDao(), linkId);
            link.tags(tags);

            String filtersSql = "SELECT id, filter FROM filters WHERE link_id = ?";
            List<Filter> filters = jdbcTemplate.query(filtersSql, new FilterMapperDao(), linkId);
            link.filters(filters);
        }

        return links;
    }

    @Transactional
    @Override
    public void update(Link link) {
        Optional<Link> optionalLink = findLinkByLinkId(link.id());
        if (optionalLink.isPresent()) {
            String query = "UPDATE " + TABLE_LINKS + " SET description = ?, updated_at = ?  WHERE id = ?";
            jdbcTemplate.update(query, link.description(), link.updatedAt(), link.id());
        }
    }
}
