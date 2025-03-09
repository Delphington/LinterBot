package backend.academy.scrapper.tracker.update.mapper;

import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.tracker.update.dto.LinkDto;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
public class LinksMapper {

    public LinkDto linkResponseToLink(final LinkResponse linkResponse) {
        return new LinkDto(linkResponse.id(), linkResponse.url(),  null, null);
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
