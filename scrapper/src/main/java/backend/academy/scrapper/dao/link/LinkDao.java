package backend.academy.scrapper.dao.link;

import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.entity.Link;
import java.util.List;
import java.util.Optional;

public interface LinkDao {
    List<Link> getListLinksByListLinkId(List<Long> ids);

    Long addLink(AddLinkRequest request);

    void remove(Long id);

    Optional<Link> findLinkByLinkId(Long id);

    List<Link> getAllLinks(int offset, int limit);

    List<Link> findAllLinksByChatIdWithFilter(int offset, int limit);

    void update(Link link);
}
