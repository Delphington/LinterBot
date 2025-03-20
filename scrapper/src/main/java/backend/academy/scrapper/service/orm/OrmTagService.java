package backend.academy.scrapper.service.orm;

import backend.academy.scrapper.dto.request.tag.TagRemoveRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.dto.response.TagListResponse;
import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.entity.Tag;
import backend.academy.scrapper.entity.TgChatLink;
import backend.academy.scrapper.exception.link.LinkNotFoundException;
import backend.academy.scrapper.exception.tag.TagNotExistException;
import backend.academy.scrapper.mapper.LinkMapper;
import backend.academy.scrapper.repository.TgChatLinkRepository;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.service.TagService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrmTagService implements TagService {

    private final LinkService linkService;
    private final TgChatLinkRepository tgChatLinkRepository;
    private final LinkMapper linkMapper;

    @Transactional
    @Override
    public ListLinksResponse getListLinkByTag(Long tgChatId, String tag) {

        List<LinkResponse> linkResponseList =
                linkService.findAllLinksByChatId(tgChatId).links();

        List<LinkResponse> ans = new ArrayList<>();

        for (LinkResponse linkResponse : linkResponseList) {
            if (linkResponse.tags().contains(tag)) {
                ans.add(linkResponse);
            }
        }
        return new ListLinksResponse(ans, linkResponseList.size());
    }

    @Transactional
    @Override
    public TagListResponse getAllListLinks(Long tgChatId) {
        List<LinkResponse> linkResponseList =
                linkService.findAllLinksByChatId(tgChatId).links();
        Set<String> tags = new HashSet<>();

        for (LinkResponse linkResponse : linkResponseList) {
            tags.addAll(linkResponse.tags());
        }
        log.info("LinkService: getAllListLinks, tags = {}", tags);
        return new TagListResponse(new ArrayList<>(tags));
    }

    @Transactional
    @Override
    public LinkResponse removeTagFromLink(Long tgChatId, TagRemoveRequest tagRemoveRequest) {
        log.info("Удаление тега из ссылки: tgChatId={}, tagRemoveRequest={}", tgChatId, tagRemoveRequest.tag());
        Optional<TgChatLink> tgChatLinkOptional = tgChatLinkRepository.findByChatIdAndLinkUrl(
                tgChatId, tagRemoveRequest.uri().toString());
        if (tgChatLinkOptional.isEmpty()) {
            log.error("Ссылка {} не найдена в чате с ID {}", tagRemoveRequest.tag(), tgChatId);
            throw new LinkNotFoundException("Ссылка " + tagRemoveRequest.tag() + " не найдена в чате с ID " + tgChatId);
        }

        TgChatLink tgChatLink = tgChatLinkOptional.orElseThrow(() -> new LinkNotFoundException("Ссылка не найдена"));
        Link link = tgChatLink.link();

        List<Tag> tagsList = link.tags();
        boolean isTagRemoved = tagsList.removeIf(tag -> tag.tag().equals(tagRemoveRequest.tag()));

        if (!isTagRemoved) {
            log.error("Тег {} не найден у ссылки в чате с ID {}", tagRemoveRequest.tag(), tgChatId);
            throw new TagNotExistException(
                    "Тег " + tagRemoveRequest.tag() + " не найден у ссылки в чате с ID " + tgChatId);
        }

        link.tags(tagsList);

        return linkMapper.linkToLinkResponse(link);
    }
}
