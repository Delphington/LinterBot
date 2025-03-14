package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.entity.Link;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.util.List;
import java.util.Optional;


public interface LinkService {

    ListLinksResponse getAllLinks(Long tgChatId);

    LinkResponse addLink(Long tgChatId, AddLinkRequest request);

    LinkResponse deleteLink(Long tgChatId, URI uri);

    Optional<Link> findById(Long id);

    List<Link> getAllLinks(int offset, int limit);

    void update(Link link);

}
