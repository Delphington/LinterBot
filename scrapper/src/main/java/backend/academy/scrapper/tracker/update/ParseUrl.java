package backend.academy.scrapper.tracker.update;

import backend.academy.scrapper.request.GitHubRequest;
import backend.academy.scrapper.request.StackOverFlowRequest;
import backend.academy.scrapper.tracker.update.exception.BadLinkRequestException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Log4j2
@Component
public class ParseUrl {
    private static final Pattern REPOSITORY_PATTERN = Pattern.compile("https://github.com/(.+?)/(.+)$");
    private static final Pattern QUESTION_PATTERN = Pattern.compile("https://stackoverflow.com/questions/(\\d+).*");


    public GitHubRequest parseUrlToGithubRequest(String url) {
        System.out.println("Смотрит какая ссылка пришла: " + url);
        if (isSupported(url, REPOSITORY_PATTERN)) {
            throw new BadLinkRequestException("Некорретная ссылка github, поддерживаются только репозитории");
        }
        try {
            String[] urlParts = url.split("/");
            return new GitHubRequest(urlParts[3], urlParts[4]);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            throw new BadLinkRequestException("Некорретная ссылка github");
        }
    }


    public StackOverFlowRequest parseUrlToStackOverFlowRequest(String url) {
        if (isSupported(url, QUESTION_PATTERN)) {
            throw new BadLinkRequestException("Некорретная ссылка stackoverflow, поддерживаются только question");
        }
        try {
            String[] urlParts = url.split("/");
            return new StackOverFlowRequest(urlParts[4]);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            throw new BadLinkRequestException("Некорректная ссылка stackoverflow");
        }
    }

    private boolean isSupported(String uri, Pattern pattern) {
        return true; //pattern.matcher(uri).matches();
    }
}

