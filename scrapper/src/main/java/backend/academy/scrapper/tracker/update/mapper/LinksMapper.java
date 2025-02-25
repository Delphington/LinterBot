package backend.academy.scrapper.tracker.update.mapper;

import backend.academy.scrapper.api.dto.response.LinkResponse;
import backend.academy.scrapper.tracker.update.dto.Link;
import org.springframework.stereotype.Component;

@Component
public class LinksMapper {

    public Link linkResponseToLink(final LinkResponse linkResponse) {
        return new Link(linkResponse.id(), linkResponse.url(), null, null);
    }
}
