package backend.academy.scrapper.api.repository;

import backend.academy.scrapper.api.entity.TgChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<TgChat, Long> {
}
