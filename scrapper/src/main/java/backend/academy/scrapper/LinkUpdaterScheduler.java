package backend.academy.scrapper;

import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.StackOverFlowClient;
import backend.academy.scrapper.request.GitHubRequest;
import backend.academy.scrapper.request.StackOverFlowRequest;
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

    /*
        Long number,        // ID вопроса
    String order,      // "desc" или "asc"
    String sort,       // "activity", "votes" и т.д.
    String site,       // "stackoverflow"
    String filter      // "withbody" для получения last_edit_date

    */
   // private StackOverFlowRequest sssss = new StackOverFlowRequest(77847901L, "desc", "activity", "stackoverflow","withbody");
   // private GitHubRequest gitHubRequest = new GitHubRequest("Delphington", "TestAPI");

    @Scheduled(fixedDelayString = "${scheduler.interval}")
    public void update() {
        log.info("что-то!");
        //  System.out.println(stackOverFlowClient.getFetchDate(sssss));
       // System.out.println(gitHubClient.getFetchDate(gitHubRequest));
    }
}
