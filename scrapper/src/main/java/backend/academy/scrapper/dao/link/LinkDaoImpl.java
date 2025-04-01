package backend.academy.scrapper.dao.link;

import backend.academy.scrapper.dao.mapper.LinkMapper;
import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.entity.Filter;
import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.entity.Tag;
import backend.academy.scrapper.exception.chat.ChatNotExistException;
import backend.academy.scrapper.exception.link.LinkNotFoundException;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
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

   // private final LinkMapper linkMapper;


    @Transactional(readOnly = true)
    @Override
    public List<Link> getListLinksByListLinkId(List<Long> ids) {

        List<Link> links = new ArrayList<>();
        for (Long id : ids) {
            Link link = findLinkByLinkId(id).orElseThrow(() -> new LinkNotFoundException("Такой ссылки нет"));

            if (link != null) {
                links.add(link);
            }
        }
        return links;
    }

    @Transactional
    @Override
    public Long addLink(AddLinkRequest request) {
        log.info("Начало добавления ссылки: {}", request.link());

        // Вставка ссылки
        jdbcTemplate.update(
                "INSERT INTO links (url, description, updated_at) VALUES (?, ?, ?)",
                request.link().toString(),
                null,
                null);

        // Получение ID вставленной записи
        Long linkId = jdbcTemplate.queryForObject(
                "SELECT id FROM links WHERE url = ? ORDER BY id DESC LIMIT 1",
                Long.class,
                request.link().toString());
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
        Optional<Link> linkOptional = jdbcTemplate
                .query(linkSql, new Object[] {id}, (rs, rowNum) -> {
                    Link link = new Link();
                    link.id(rs.getLong("id"));
                    link.url(rs.getString("url"));
                    link.description(rs.getString("description"));

                    // Обработка NULL для updated_at
                    Timestamp updatedAtTimestamp = rs.getTimestamp("updated_at");
                    if (updatedAtTimestamp != null) {
                        link.updatedAt(updatedAtTimestamp
                                .toInstant()
                                .atOffset(ZoneOffset.UTC)); // Преобразуем в OffsetDateTime
                    } else {
                        link.updatedAt(null); // Устанавливаем null, если updated_at равен NULL
                    }
                    return link;
                })
                .stream()
                .findFirst();

        if (linkOptional.isEmpty()) {
            return Optional.empty();
        }

        Link link = linkOptional.orElseThrow(() -> new LinkNotFoundException("Ссылка с ID не найдена"));

        // Запрос для получения тегов
        String tagsSql = "SELECT id, tag FROM " + TABLE_TAGS + " WHERE link_id = ?";
        List<Tag> tags = jdbcTemplate.query(tagsSql, new Object[] {id}, (rs, rowNum) -> {
            Tag tag = new Tag();
            tag.id(rs.getLong("id"));
            tag.tag(rs.getString("tag"));
            tag.link(link);
            return tag;
        });
        link.tags(tags);

        // Запрос для получения фильтров
        String filtersSql = "SELECT id, filter FROM " + TABLE_FILTERS + " WHERE link_id = ?";
        List<Filter> filters = jdbcTemplate.query(filtersSql, new Object[] {id}, (rs, rowNum) -> {
            Filter filter = new Filter();
            filter.id(rs.getLong("id"));
            filter.filter(rs.getString("filter"));
            filter.link(link);
            return filter;
        });
        link.filters(filters);

        return Optional.of(link);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Link> getAllLinks(int offset, int limit) {
        // Запрос для получения данных о ссылках
        String linksSql = "SELECT id, url, description, updated_at FROM links LIMIT ? OFFSET ?";
        List<Link> links = jdbcTemplate.query(linksSql, new Object[] {limit, offset}, (rs, rowNum) -> {
            Link link = new Link();
            link.id(rs.getLong("id"));
            link.url(rs.getString("url"));
            link.description(rs.getString("description"));

            Timestamp updatedAtTimestamp = rs.getTimestamp("updated_at");
            if (updatedAtTimestamp != null) {
                link.updatedAt(updatedAtTimestamp.toInstant().atOffset(ZoneOffset.UTC));
            } else {
                log.warn("Поле updated_at равно null для ссылки с id = {}", link.id());
                link.updatedAt(null); // или установите значение по умолчанию
            }

            return link;
        });

        // Для каждой ссылки получаем теги и фильтры
        for (Link link : links) {
            Long linkId = link.id();

            // Запрос для получения тегов
            String tagsSql = "SELECT id, tag FROM tags WHERE link_id = ?";
            List<Tag> tags = jdbcTemplate.query(tagsSql, new Object[] {linkId}, (rs, rowNum) -> {
                Tag tag = new Tag();
                tag.id(rs.getLong("id"));
                tag.tag(rs.getString("tag"));
                tag.link(link);
                return tag;
            });
            link.tags(tags);

            // Запрос для получения фильтров
            String filtersSql = "SELECT id, filter FROM filters WHERE link_id = ?";
            List<Filter> filters = jdbcTemplate.query(filtersSql, new Object[] {linkId}, (rs, rowNum) -> {
                Filter filter = new Filter();
                filter.id(rs.getLong("id"));
                filter.filter(rs.getString("filter"));
                filter.link(link);
                return filter;
            });
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
