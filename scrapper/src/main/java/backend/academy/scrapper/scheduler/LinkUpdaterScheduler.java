package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.entity.Link;
import backend.academy.scrapper.mapper.LinkMapper;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.tracker.update.LinkUpdateProcessor;
import backend.academy.scrapper.tracker.update.dto.LinkDto;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkUpdaterScheduler {

    private final LinkUpdateProcessor linkUpdateProcessor;
    private final LinkMapper linksMapper;
    private final LinkService linkService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private static final int COUNT_THREAD = 4;

    private final AtomicInteger githubProcessedLinksCounter;
    private final AtomicInteger stackoverflowProcessedLinksCounter;

    @Value("${scheduler.batch-size}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${scheduler.interval}")
    public void update() {
        log.info("Проверка обновления");

        githubProcessedLinksCounter.set(0);
        stackoverflowProcessedLinksCounter.set(0);

        int offset = 0;
        List<Link> links;

        do {
            // Получаем батч линков
            links = linkService.findAllLinksByChatIdWithFilter(offset, batchSize);

            List<LinkDto> linkDtoList = linksMapper.listLinkToListLinkDto(links);
            List<List<LinkDto>> batches = splitIntoBatches(linkDtoList, COUNT_THREAD);

            List<CompletableFuture<Void>> futures = batches.stream()
                    .map(batch ->
                            CompletableFuture.runAsync(() -> linkUpdateProcessor.updateLink(batch), executorService))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            log.info("Ссылки на обновления: {}", linkDtoList);

            linkUpdateProcessor.updateLink(linkDtoList);
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
