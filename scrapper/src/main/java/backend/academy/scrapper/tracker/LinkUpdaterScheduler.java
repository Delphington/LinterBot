package backend.academy.scrapper.tracker;

import backend.academy.scrapper.api.entity.Link;
import backend.academy.scrapper.api.service.LinkService;
import backend.academy.scrapper.tracker.update.UpdaterLinks;
import backend.academy.scrapper.tracker.update.dto.LinkDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkUpdaterScheduler {

    private final UpdaterLinks updaterLinks;

    private final LinkService linkService;

    //  private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    private List<LinkDto> mapperToLinkDto(List<Link> list) {
        List<LinkDto> linkDtoList = new ArrayList<>();
        for (Link link : list) {
            LinkDto linkDto = new LinkDto(link.id(), URI.create(link.url().trim()), link.updatedAt(), link.description());
            linkDtoList.add(linkDto);
        }
        return linkDtoList;
    }


    @Scheduled(fixedDelayString = "${scheduler.interval}")
    public void update() {
        log.info("Проверка обновления");

        List<Link> lists = linkService.getAllLinks();

        List<LinkDto> listssss = mapperToLinkDto(lists);
//        System.err.println("Link List: " + lists);
//        System.err.println("LinkDto List: " + listssss);
        updaterLinks.updateLink(listssss);



//        //Ссылки поделенные на 4
//        List<List<Link>> batches = splitIntoBatches(updateLinkService.linkList(), 4);
//
//        List<CompletableFuture<Void>> futures = batches.stream()
//            .map(batch -> CompletableFuture.runAsync(() -> updaterLinks.updateLink(batch), executorService)).toList();
//
//        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

//    private List<List<Link>> splitIntoBatches(List<Link> linkList, int batchCount) {
//        int batchSize = (linkList.size() + batchCount - 1) / batchCount; // Вычисляем размер каждой части
//        List<List<Link>> batches = new ArrayList<>();
//
//        for (int i = 0; i < linkList.size(); i += batchSize) {
//            batches.add(linkList.subList(i, Math.min(i + batchSize, linkList.size())));
//        }
//
//        return batches;
//    }
}
