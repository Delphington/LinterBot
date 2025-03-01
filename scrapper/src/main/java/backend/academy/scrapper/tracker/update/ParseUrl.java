package backend.academy.scrapper.tracker.update;

import backend.academy.scrapper.request.GitHubRequest;
import backend.academy.scrapper.request.StackOverFlowRequest;
import backend.academy.scrapper.tracker.update.exception.BadLinkRequestException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ParseUrl {

    public GitHubRequest parseUrlToGithubRequest(String url) {
        if (url == null) {
            throw new BadLinkRequestException("Некорретная ссылка github: URL не может быть null");
        }

        try {
            String[] urlParts = url.split("/");
            return new GitHubRequest(urlParts[3], urlParts[4]);
        } catch (RuntimeException e) {
            throw new BadLinkRequestException("Некорретная ссылка github");
        }
    }

    public StackOverFlowRequest parseUrlToStackOverFlowRequest(String url) {
        if (url == null) {
            throw new BadLinkRequestException("Некорретная ссылка stackOverFlow: URL не может быть null");
        }

        try {
            String[] urlParts = url.split("/");
            return new StackOverFlowRequest(urlParts[4]);
        } catch (RuntimeException e) {
            throw new BadLinkRequestException("Некорректная ссылка stackoverflow");
        }
    }
}
