package backend.academy.scrapper.api.mapper;

import backend.academy.scrapper.api.dto.request.AddLinkRequest;
import backend.academy.scrapper.api.dto.response.LinkResponse;
import backend.academy.scrapper.api.entity.Link;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
public class LinkMapper {

    public LinkResponse LinkToLinkResponse(Link link) {
        return new LinkResponse(link.id(), URI.create(link.url()), link.tags(), link.filters());
    }

    public List<LinkResponse> LinkListToLinkResponseList(List<Link> linkList) {
        List<LinkResponse> list = new ArrayList<>();
        for (Link link : linkList) {
            list.add(LinkToLinkResponse(link));
        }
        return list;
    }

}
