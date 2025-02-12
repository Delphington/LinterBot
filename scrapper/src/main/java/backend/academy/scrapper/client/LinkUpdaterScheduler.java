package backend.academy.scrapper.client;

import backend.academy.scrapper.client.tracker.UpdateLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkUpdaterScheduler {

    //private final GitHubClient gitHubClient;
    // private final StackOverFlowClient stackOverFlowClient;
//https://api.stackexchange.com/2.3/questions/77847901?order=desc&sort=activity&site=stackoverflow&filter=withbody

    private final UpdateLinkService updateLinkService;

    @Scheduled(fixedDelayString = "${scheduler.interval}")
    public void update() {
        log.info("что-то!");
        updateLinkService.updateLink();

        //   telegramBotClient.addUpdate();
        //  System.out.println(stackOverFlowClient.getFetchDate(sssss));
        // System.out.println(gitHubClient.getFetchDate(gitHubRequest));
    }
}
