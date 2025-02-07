package backend.academy.scrapper;//package backend.academy.scrapper.github.TEST.service;
//
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkUpdaterScheduler {

    private final GitHubService gitHubClient;

    @Scheduled(fixedDelayString = "${app.scheduler.interval}")
    public void update() {
        log.info("что-то!");
        System.out.println( gitHubClient.getFetchDate());
    }
}
