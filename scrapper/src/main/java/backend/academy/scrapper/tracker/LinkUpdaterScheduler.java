package backend.academy.scrapper.tracker;

import backend.academy.scrapper.tracker.update.UpdaterLinks;
import backend.academy.scrapper.tracker.update.dto.Link;
import backend.academy.scrapper.tracker.update.service.UpdateLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final UpdateLinkService updateLinkService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Scheduled(fixedDelayString = "${scheduler.interval}")
    public void update() {
        log.info("Проверка обновления");

        //Ссылки поделенные на 4
        List<List<Link>> batches = splitIntoBatches(updateLinkService.linkList(), 4);

        List<CompletableFuture<Void>> futures = batches.stream()
            .map(batch -> CompletableFuture.runAsync(() -> updaterLinks.updateLink(batch), executorService)).toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private List<List<Link>> splitIntoBatches(List<Link> linkList, int batchCount) {
        int batchSize = (linkList.size() + batchCount - 1) / batchCount; // Вычисляем размер каждой части
        List<List<Link>> batches = new ArrayList<>();

        for (int i = 0; i < linkList.size(); i += batchSize) {
            batches.add(linkList.subList(i, Math.min(i + batchSize, linkList.size())));
        }

        return batches;
    }
}
