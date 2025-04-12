package backend.academy.scrapper.service.jdbc;

import backend.academy.scrapper.dao.TgChatLinkDao;
import backend.academy.scrapper.dao.filter.FilterDao;
import backend.academy.scrapper.dao.link.LinkDao;
import backend.academy.scrapper.dao.tag.TagDao;
import backend.academy.scrapper.dto.request.tag.TagRemoveRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.dto.response.TagListResponse;
import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.entity.Tag;
import backend.academy.scrapper.exception.link.LinkNotFoundException;
import backend.academy.scrapper.exception.tag.TagNotExistException;
import backend.academy.scrapper.mapper.LinkMapper;
import backend.academy.scrapper.service.TagService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
public class JdbcTagService implements TagService {

    private final FilterDao filterDao;
    private final TagDao tagDao;
    private final LinkDao linkDao;
    private final TgChatLinkDao tgChatLinkDao;
    private final LinkMapper linkMapper;

    @Override
    public ListLinksResponse getListLinkByTag(Long tgChatId, String tag) {
        List<Long> linkIdsList = tgChatLinkDao.getLinkIdsByChatId(tgChatId);

        List<Link> linkList = linkDao.getListLinksByListLinkId(linkIdsList);

        List<LinkResponse> linkResponseList = new ArrayList<>();

        for (Link item : linkList) {
            List<Tag> tagList = tagDao.findListTagByLinkId(item.id());
            for (Tag itemTag : tagList) {
                if (itemTag.tag().equals(tag)) {
                    item.filters(filterDao.findListFilterByLinkId(item.id()));
                    item.tags(tagList);
                    linkResponseList.add(linkMapper.linkToLinkResponse(item));
                }
            }
        }

        return new ListLinksResponse(linkResponseList, linkList.size());
    }

    @Override
    public TagListResponse getAllListLinks(Long tgChatId) {
        List<Link> linkList = linkDao.getListLinksByListLinkId(tgChatLinkDao.getLinkIdsByChatId(tgChatId));
        Set<String> tagsSet = new HashSet<>();
        for (Link link : linkList) {
            List<Tag> tagList = tagDao.findListTagByLinkId(link.id());
            tagList.forEach(tag -> tagsSet.add(tag.tag()));
        }
        return new TagListResponse(new ArrayList<>(tagsSet));
    }

    @Override
    public LinkResponse removeTagFromLink(Long tgChatId, TagRemoveRequest tagRemoveRequest) {
        List<Link> linkList = linkDao.getListLinksByListLinkId(tgChatLinkDao.getLinkIdsByChatId(tgChatId));

        Optional<Link> optLink = linkList.stream()
                .filter(link -> link.url().equals(tagRemoveRequest.uri().toString()))
                .findFirst();

        if (optLink.isEmpty()) {
            log.warn("Ссылка {} не найдена в чате {}", tagRemoveRequest.uri(), tgChatId);
            throw new LinkNotFoundException(
                    "Ссылка " + tagRemoveRequest.uri() + " не найдена в чате с ID " + tgChatId + ".");
        }

        Link link = optLink.orElseThrow(() -> new LinkNotFoundException("Ссылка не найдена"));

        List<Tag> tagsList = tagDao.findListTagByLinkId(link.id());

        boolean isTagRemoved = tagsList.removeIf(tag -> tag.tag().equals(tagRemoveRequest.tag()));

        if (!isTagRemoved) {
            log.error("Тег {} не найден у ссылки в чате с ID {}", tagRemoveRequest.tag(), tgChatId);
            throw new TagNotExistException(
                    "Тег " + tagRemoveRequest.tag() + " не найден у ссылки в чате с ID " + tgChatId);
        }
        tagDao.removeTag(link.id(), tagRemoveRequest.tag());
        link.tags(tagsList);
        link.filters(filterDao.findListFilterByLinkId(link.id()));

        return linkMapper.linkToLinkResponse(link);
    }
}
