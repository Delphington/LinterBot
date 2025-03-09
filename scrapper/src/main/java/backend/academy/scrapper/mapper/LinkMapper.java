package backend.academy.scrapper.mapper;

import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.tracker.update.dto.LinkDto;
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

    public List<LinkDto> listLinkToListLinkDto(List<Link> list) {
        List<LinkDto> linkDtoList = new ArrayList<>();
        for (Link link : list) {
            LinkDto linkDto = new LinkDto(link.id(), URI.create(link.url().trim()), link.updatedAt(), link.description());
            linkDtoList.add(linkDto);
        }
        return linkDtoList;
    }

}
