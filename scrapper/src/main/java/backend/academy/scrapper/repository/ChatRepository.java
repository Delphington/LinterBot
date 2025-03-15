package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.TgChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<TgChat, Long> {}
