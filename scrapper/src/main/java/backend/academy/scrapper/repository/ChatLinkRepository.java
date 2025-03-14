package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.TgChatLink;
import backend.academy.scrapper.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatLinkRepository extends JpaRepository<TgChatLink, Long> {

    @Query("SELECT cl.link FROM TgChatLink cl WHERE cl.tgChat.id = :chatId")
    List<Link> findLinksByChatId(@Param("chatId") Long chatId);

//
    @Query("SELECT cl FROM TgChatLink cl WHERE cl.tgChat.id = :chatId AND cl.link.url = :url")
    Optional<TgChatLink> findByChatIdAndLinkUrl(@Param("chatId") Long chatId, @Param("url") String url);

    @Query("SELECT COUNT(cl) FROM TgChatLink cl WHERE cl.link.id = :linkId")
    long countByLinkId(@Param("linkId") Long linkId);
//
    // Метод для получения списка id чатов по id ссылки
    @Query("SELECT cl.tgChat.id FROM TgChatLink cl WHERE cl.link.id = :linkId")
    List<Long> findChatIdsByLinkId(@Param("linkId") Long linkId);
}
