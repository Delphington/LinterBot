package backend.academy.scrapper.client.tracker;

import backend.academy.scrapper.request.GitHubRequest;
import backend.academy.scrapper.request.StackOverFlowRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ParseService{
    public GitHubRequest parseUrlToGithubRequest(String url) {
        try {
            String[] urlParts = url.split("/");
            log.error("==Удачно преобразовали в  GithubRequest");
            return new GitHubRequest(urlParts[3], urlParts[4]);
        } catch (NullPointerException | IndexOutOfBoundsException  e) {
            throw new BadLinkRequestException("Некорретная ссылка github");
        }
    }


    public StackOverFlowRequest parseUrlToStackOverFlowRequest(String url) {
        try {
            String[] urlParts = url.split("/");
            return new StackOverFlowRequest(urlParts[4]);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            throw new BadLinkRequestException("Некорректная ссылка stackoverflow");
        }
    }
}

