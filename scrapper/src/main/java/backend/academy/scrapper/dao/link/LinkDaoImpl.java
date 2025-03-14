package backend.academy.scrapper.dao.link;

import backend.academy.scrapper.dao.mapper.MapperLinkDao;
import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.entity.Link;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class LinkDaoImpl implements LinkDao {
    private final JdbcTemplate jdbcTemplate;
    private final static String TABLE_NAME = "link";

    @Override
    public List<Link> getLinkById(List<Long> ids) {

        List<Link> links = new ArrayList<>();
        for (Long id : ids) {
            Link link = findLinkByLinkId(id).get();
            if (link != null) {
                links.add(link);
            }
        }
        return links;
    }


    @Override
    public Long addLink(AddLinkRequest request) {
        log.info("Начало добавления ссылки: {}", request.link());
        // SQL-запрос для вставки данных
        String sql = "INSERT INTO " + TABLE_NAME + " (url, tags, filters, description, updated_at) VALUES (?, ?, ?, ?, ?)";

        // Используем KeyHolder для получения ID новой записи
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Выполняем вставку
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, request.link().toString()); // url
            ps.setArray(2, connection.createArrayOf("TEXT", request.tags().toArray())); // tags
            ps.setArray(3, connection.createArrayOf("TEXT", request.filters().toArray())); // filters
            ps.setObject(4, null); // description
            ps.setObject(5, null); // updated_at
            return ps;
        }, keyHolder);



        System.err.println("Запись вставилась в БД link, id link = " + keyHolder.getKey().longValue()); // Оставьте эту строку временно, пока не убедитесь, что логи работают

        return keyHolder.getKey().longValue();
    }

    @Override
    public void remove(Long id) {
        log.info("Удаление записи из таблицы {} с ID: {}", TABLE_NAME, id);
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }


    @Override
    public Optional<Link> findLinkByLinkId(Long id) {
        String sql = "SELECT id, url, tags, filters, description, updated_at FROM " + TABLE_NAME + "  WHERE id = ?";
        return jdbcTemplate.query(sql, new Object[]{id}, new MapperLinkDao())
            .stream()
            .findFirst();
    }

    @Override
    public List<Link> getAllLinks(int offset, int limit) {
        String sql = "SELECT id, url, tags, filters, description, updated_at FROM " + TABLE_NAME + " LIMIT ? OFFSET ?";;
        return jdbcTemplate.query(sql, new MapperLinkDao(), limit, offset);
    }

    @Override
    public void update(Link link) {
        Optional<Link> optionalLink = findLinkByLinkId(link.id());
        if (optionalLink.isPresent()) {
            String query = "UPDATE " + TABLE_NAME + " SET description = ?, updated_at = ?  WHERE id = ?";
            jdbcTemplate.update(query, link.description(), link.updatedAt(), link.id());
        }
    }
}
