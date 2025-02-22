package backend.academy.scrapper.tracker;

import backend.academy.scrapper.tracker.update.UpdaterLinks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkUpdaterScheduler {

//https://api.stackexchange.com/2.3/questions/77847901?order=desc&sort=activity&site=stackoverflow&filter=withbody
    private final UpdaterLinks updaterLinks;

    @Scheduled(fixedDelayString = "${scheduler.interval}")
    public void update() {
        log.info("что-то!");
        updaterLinks.updateLink();

    }
}
