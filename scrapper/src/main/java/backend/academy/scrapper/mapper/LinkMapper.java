package backend.academy.scrapper.mapper;

import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.entity.Filter;
import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.entity.Tag;
import backend.academy.scrapper.tracker.update.dto.LinkDto;
import jakarta.persistence.TableGenerator;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LinkMapper {

    public LinkResponse LinkToLinkResponse(Link link) {
        List<String> tags = link.tags().stream().map(Tag::tag).toList();
        List<String> filters = link.filters().stream().map(Filter::filter).toList();
        System.out.println("tags: " + tags);
        System.out.println("filters: " + filters);
        return new LinkResponse(link.id(), URI.create(link.url()), tags, filters);
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
