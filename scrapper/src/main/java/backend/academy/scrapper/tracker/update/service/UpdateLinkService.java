package backend.academy.scrapper.tracker.update.service;

import backend.academy.scrapper.api.dto.response.LinkResponse;
import backend.academy.scrapper.tracker.update.dto.LinkDto;
import backend.academy.scrapper.tracker.update.mapper.LinksMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UpdateLinkService {

    @Getter
    private final List<LinkDto> linkList = new ArrayList<>();

    private final LinksMapper linkMapper;

    public void addLink(LinkResponse linkResponse) {
        LinkDto link = linkMapper.linkResponseToLink(linkResponse);
        linkList.add(link);
    }

    public void deleteLink(LinkResponse linkResponse) {
        linkList.remove(linkMapper.linkResponseToLink(linkResponse));
    }
}
