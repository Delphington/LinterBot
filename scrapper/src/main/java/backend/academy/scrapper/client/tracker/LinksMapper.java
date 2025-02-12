package backend.academy.scrapper.client.tracker;

import backend.academy.scrapper.api.dto.response.LinkResponse;
import org.springframework.stereotype.Component;


@Component
public class LinksMapper {

    public Link linkResponseToLink(final LinkResponse linkResponse) {
        return new Link(linkResponse.id(), linkResponse.url(), null, null);
    }

}
