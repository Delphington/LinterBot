package backend.academy.scrapper.client.tracker;

import backend.academy.scrapper.api.dto.response.LinkResponse;
import backend.academy.scrapper.api.service.LinkService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UpdateLinkService {

//    public class Link {
//        private Long id;
//        private URI url;
//        private OffsetDateTime lastUpdatedTime;
//        private OffsetDateTime createdAt;
//    }

    @Getter
    private final List<Link> linkList = new ArrayList<>();

    private final LinksMapper linkMapper;

    public void addLink(LinkResponse linkResponse) {
        Link link = linkMapper.linkResponseToLink(linkResponse);
        linkList.add(link);
    }

    public void deleteLink(LinkResponse linkResponse) {
        linkList.remove(linkMapper.linkResponseToLink(linkResponse));
    }

}
