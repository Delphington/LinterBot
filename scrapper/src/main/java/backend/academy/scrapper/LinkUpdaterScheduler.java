package backend.academy.scrapper;//package backend.academy.scrapper.github.TEST.service;
//

import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.request.GitHubRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkUpdaterScheduler {

    private final GitHubClient gitHubClient;
    private GitHubRequest gitHubRequest = new GitHubRequest("Delphington", "TestAPI");


    @Scheduled(fixedDelayString = "${scheduler.interval}")
    public void update() {
        log.info("что-то!");
        System.out.println(gitHubClient.getFetchDate(gitHubRequest));
    }
}
