package backend.academy.scrapper.tracker.update.mapper;

import backend.academy.scrapper.api.dto.response.LinkResponse;
import backend.academy.scrapper.tracker.update.dto.LinkDto;
import org.springframework.stereotype.Component;

@Component
public class LinksMapper {

    public LinkDto linkResponseToLink(final LinkResponse linkResponse) {
        return new LinkDto(linkResponse.id(), linkResponse.url(),  null, null);
    }
}
