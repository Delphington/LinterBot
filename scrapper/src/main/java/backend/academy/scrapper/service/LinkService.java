package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.entity.Link;
import java.net.URI;
import java.util.List;
import java.util.Optional;

public interface LinkService {

    ListLinksResponse findAllLinksByChatId(Long tgChatId);

    LinkResponse addLink(Long tgChatId, AddLinkRequest request);

    LinkResponse deleteLink(Long tgChatId, URI uri);

    Optional<Link> findById(Long id);

    List<Link> findAllLinksByChatId(int offset, int limit);

    public List<Link> findAllLinksByChatIdWithFilter(int offset, int limit);

    void update(Link link);
}
