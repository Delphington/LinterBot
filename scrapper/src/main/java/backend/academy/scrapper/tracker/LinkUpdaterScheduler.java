package backend.academy.scrapper.tracker;

import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.tracker.update.UpdaterLinks;
import backend.academy.scrapper.tracker.update.dto.LinkDto;
import backend.academy.scrapper.tracker.update.mapper.LinksMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkUpdaterScheduler {

    private final UpdaterLinks updaterLinks;
    private final LinksMapper linksMapper;
    private final LinkService linkService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final static int COUNT_THREAD  = 4;

    @Value("${scheduler.batch-size}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${scheduler.interval}")
    public void update() {
        log.info("Проверка обновления");

        int offset = 0;
        List<Link> links;

        do {
            //Получаем батч линков
            links = linkService.getAllLinks(offset, batchSize);
            List<LinkDto> linkDtoList = linksMapper.listLinkToListLinkDto(links);
            List<List<LinkDto>> batches = splitIntoBatches(linkDtoList, COUNT_THREAD);

            List<CompletableFuture<Void>> futures = batches.stream()
                .map(batch -> CompletableFuture.runAsync(() -> updaterLinks.updateLink(batch), executorService)).toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            log.info("Ссылки на обновления: {}", linkDtoList);

            updaterLinks.updateLink(linkDtoList);
            offset += batchSize;
        } while (!links.isEmpty());
    }

    private List<List<LinkDto>> splitIntoBatches(List<LinkDto> linkList, int countTread) {
        int batchSize = (linkList.size() + countTread - 1) / countTread;
        List<List<LinkDto>> batches = new ArrayList<>();

        for (int i = 0; i < linkList.size(); i += batchSize) {
            batches.add(linkList.subList(i, Math.min(i + batchSize, linkList.size())));
        }
        return batches;
    }
}
