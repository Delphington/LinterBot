package backend.academy.scrapper.util;

import backend.academy.scrapper.tracker.request.GitHubRequest;
import backend.academy.scrapper.tracker.request.StackOverFlowRequest;
import backend.academy.scrapper.tracker.update.exception.BadLinkRequestException;
import java.util.Arrays;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {
    public static String sanitize(Long id) {
        return String.valueOf(id).replace("\r", "").replace("\n", "");
    }

    public static List<String> getStackTrace(Exception ex) {
        return Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
    }
    // -----------------------------------

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
