package backend.academy.scrapper.api.repository;

import backend.academy.scrapper.api.entity.ChatLink;
import backend.academy.scrapper.api.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatLinkRepository extends JpaRepository<ChatLink, Long> {

    @Query("SELECT cl.link FROM ChatLink cl WHERE cl.chat.id = :chatId")
    List<Link> findLinksByChatId(@Param("chatId") Long chatId);


    @Query("SELECT cl.link FROM ChatLink cl WHERE cl.chat.id = :chatId AND cl.link.url = :url")
    Optional<Link> findLinkByChatIdAndUrl(@Param("chatId") Long chatId, @Param("url") String url);



    @Query("SELECT cl FROM ChatLink cl WHERE cl.chat.id = :chatId AND cl.link.url = :url")
    Optional<ChatLink> findByChatIdAndLinkUrl(@Param("chatId") Long chatId, @Param("url") String url);


// Метод для подсчета количества связей по linkId
    @Query("SELECT COUNT(cl) FROM ChatLink cl WHERE cl.link.id = :linkId")
    long countByLinkId(@Param("linkId") Long linkId);
}
