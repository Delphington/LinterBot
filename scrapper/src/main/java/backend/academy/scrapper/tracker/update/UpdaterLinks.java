package backend.academy.scrapper.tracker.update;

import backend.academy.scrapper.api.service.LinkService;
import backend.academy.scrapper.request.GitHubRequest;
import backend.academy.scrapper.request.StackOverFlowRequest;
import backend.academy.scrapper.response.GitHubResponse;
import backend.academy.scrapper.response.StackOverFlowResponse;
import backend.academy.scrapper.tracker.client.GitHubClient;
import backend.academy.scrapper.tracker.client.StackOverFlowClient;
import backend.academy.scrapper.tracker.client.TelegramBotClient;
import backend.academy.scrapper.tracker.update.dto.Link;
import backend.academy.scrapper.tracker.update.exception.BadLinkRequestException;
import backend.academy.scrapper.tracker.update.model.LinkUpdate;
import backend.academy.scrapper.tracker.update.service.UpdateLinkService;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UpdaterLinks {
    private final TelegramBotClient telegramBotClient;

    private final UpdateLinkService updateLinkService;
    private final ParseUrl parseUrl;

    private final GitHubClient gitHubClient;
    private final StackOverFlowClient stackOverFlowClient;
    private final LinkService linkService;

    private List<Link> updatedLinkList;

    public void updateLink() {
        updatedLinkList = new ArrayList<>();
        for (Link link : updateLinkService.linkList()) {
            if (link.url().toString().contains("github")) {
                handlerUpdateGitHub(link);
            } else if (link.url().toString().contains("stackoverflow")) {
                handlerUpdateStackOverFlow(link);
            } else {
                throw new BadLinkRequestException("Ссылка не может быть обработана, "
                    + "так как это не github и не stackoverflow");
            }

        }

        if (!updatedLinkList.isEmpty()) {
            for (Link item : updatedLinkList) {
                List<Long> chatIds = linkService.findIdChatsByUrlId(item.id());
                telegramBotClient.addUpdate(new LinkUpdate(item.id(), item.url(), "Обновление", chatIds));
            }
        }

    }


    private void handlerUpdateGitHub(Link link) {
        GitHubRequest gitHubRequest = parseUrl.parseUrlToGithubRequest(link.url().toString());

        GitHubResponse gitHubResponse = gitHubClient.getFetchDate(gitHubRequest);

        Optional<Link> optional = checkUpdateLinkGitHub(link, gitHubResponse);
        optional.ifPresent(value -> updatedLinkList.add(value));
    }


    private void handlerUpdateStackOverFlow(Link link) {
        StackOverFlowRequest stackOverFlowRequest =
            parseUrl.parseUrlToStackOverFlowRequest(link.url().toString());
        StackOverFlowResponse stackOverFlowResponse = stackOverFlowClient.getFetchDate(stackOverFlowRequest);

        Optional<Link> optional = checkUpdateLinkStackOverFlow(link, stackOverFlowResponse);
        optional.ifPresent(value -> updatedLinkList.add(value));
    }

    private Optional<Link> checkUpdateLinkGitHub(Link link, GitHubResponse gitHubResponse) {
        if (link.createdAt() == null) {
            //Если не разу не обновляли
            link.createdAt(OffsetDateTime.now());
            link.lastUpdatedTime(gitHubResponse.updated());
            log.info("handler первое заполнили время создания");
            return Optional.empty();
        }

        //произошло изменение
        if (!link.lastUpdatedTime().equals(gitHubResponse.updated())) {
            log.info("handler отправили изменения");
            link.lastUpdatedTime(gitHubResponse.updated());
            return Optional.of(link);
        }
        //ничего не произошло
        return Optional.empty();
    }


    private Optional<Link> checkUpdateLinkStackOverFlow(Link link, StackOverFlowResponse stackOverFlowResponse) {
        if (link.createdAt() == null) {
            link.createdAt(OffsetDateTime.now());
            link.lastUpdatedTime(stackOverFlowResponse.items().get(0).lastActivityDate());
            log.info("checkUpdateLinkStackOverFlow первое заполнили время создания");
            return Optional.empty();
        }

        //произошло изменение
        if (!link.lastUpdatedTime().equals(stackOverFlowResponse.items().get(0).lastActivityDate())) {
            link.lastUpdatedTime(stackOverFlowResponse.items().get(0).lastActivityDate());
            return Optional.of(link);
        }
        //ничего не произошло
        return Optional.empty();
    }

}
