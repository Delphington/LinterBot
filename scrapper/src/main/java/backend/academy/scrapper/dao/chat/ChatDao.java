package backend.academy.scrapper.dao.chat;

public interface ChatDao {
    boolean isExistChat(Long id);

    void save(Long id);

    void remove(Long id);
}
