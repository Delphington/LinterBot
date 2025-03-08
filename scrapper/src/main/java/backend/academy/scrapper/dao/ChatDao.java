package backend.academy.scrapper.dao;

import org.springframework.stereotype.Repository;

public interface ChatDao {
    boolean isExistChat(Long id);

    void save(Long id);

    void remove(Long id);
}
