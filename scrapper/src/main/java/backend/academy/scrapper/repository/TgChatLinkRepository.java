package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.entity.TgChatLink;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TgChatLinkRepository extends JpaRepository<TgChatLink, Long> {

    @Query("SELECT cl.link FROM TgChatLink cl WHERE cl.tgChat.id = :chatId")
    List<Link> findLinksByChatId(@Param("chatId") Long chatId);

    @Query("SELECT cl FROM TgChatLink cl " + "JOIN cl.link l " + "WHERE cl.tgChat.id = :chatId AND l.url = :url")
    Optional<TgChatLink> findByChatIdAndLinkUrl(@Param("chatId") Long chatId, @Param("url") String url);

    @Query("SELECT COUNT(cl) FROM TgChatLink cl WHERE cl.link.id = :linkId")
    long countByLinkId(@Param("linkId") Long linkId);

    // Метод для получения списка chatId чатов по chatId ссылки
    @Query("SELECT cl.tgChat.id FROM TgChatLink cl WHERE cl.link.id = :linkId")
    List<Long> findChatIdsByLinkId(@Param("linkId") Long linkId);
}
