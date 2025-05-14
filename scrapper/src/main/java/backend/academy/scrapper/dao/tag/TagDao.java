package backend.academy.scrapper.dao.tag;

import backend.academy.scrapper.entity.Tag;
import java.util.List;

public interface TagDao {

    List<Tag> findListTagByLinkId(Long id);

    void removeTag(Long id, String removedTag);
}
