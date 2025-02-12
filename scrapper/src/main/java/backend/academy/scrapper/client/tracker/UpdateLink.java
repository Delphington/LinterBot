package backend.academy.scrapper.client.tracker;

import backend.academy.scrapper.api.service.LinkService;
import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.StackOverFlowClient;
import backend.academy.scrapper.client.bot.TelegramBotClient;
import backend.academy.scrapper.request.GitHubRequest;
import backend.academy.scrapper.request.StackOverFlowRequest;
import backend.academy.scrapper.response.GitHubResponse;
import backend.academy.scrapper.response.StackOverFlowResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import java.awt.LinearGradientPaint;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//
@Log4j2
@RequiredArgsConstructor
@Component
public class UpdateLink {
    private final TelegramBotClient telegramBotClient;

    private final UpdateLinkService updateLinkService;
    private final ParseService parseService;

    private final GitHubClient gitHubClient;
    private final StackOverFlowClient stackOverFlowClient;
    private final LinkService linkService;

    public void updateLink() {
        List<Link> linkList = updateLinkService.linkList(); //Получаем лист всех ссылок

        List<Link> updatedLinkList = new ArrayList<>();

        log.error("==UpdateLink ::  updateLink");

        for (Link link : linkList) {
            //костыль для гитхаба
            if (link.url().toString().contains("github")) {
                //todo: не отлавливаем исключения
                GitHubRequest gitHubRequest = parseService.parseUrlToGithubRequest(link.url().toString());
                log.warn("==UpdateLink ::  gitHubRequest = " + gitHubRequest);

                GitHubResponse gitHubResponse = gitHubClient.getFetchDate(gitHubRequest);
                log.warn("==UpdateLink ::  gitHubResponse = " + gitHubResponse);

                Optional<Link> optional = checkUpdateLink(link, gitHubResponse);
                if (optional.isPresent()) {
                    updatedLinkList.add(optional.get());
                }
            } else {
                /// stackoverFlow

                StackOverFlowRequest stackOverFlowRequest =
                    parseService.parseUrlToStackOverFlowRequest(link.url().toString());
                log.warn("==UpdateLink ::  StackOverFlowRequest = " + stackOverFlowRequest);


                StackOverFlowResponse stackOverFlowResponse  = stackOverFlowClient.getFetchDate(stackOverFlowRequest);
                log.warn("==UpdateLink ::  stackOverFlowResponse = " + stackOverFlowResponse);

                Optional<Link> optional = checkUpdateLinkStack(link, stackOverFlowResponse);
                if (optional.isPresent()) {
                    updatedLinkList.add(optional.get());
                }
            }

        }

        if (!updatedLinkList.isEmpty()) {
            for (Link item : updatedLinkList) {
                List<Long> chatIds = linkService.findIdChatsByUrlId(item.id());
                telegramBotClient.addUpdate(new UpdateLinkResponse(item.id(), item.url(), "Обноваление", chatIds));
            }
        }

    }

    private Optional<Link> checkUpdateLink(Link link, GitHubResponse gitHubResponse) {
        if (link.createdAt() == null) {
            //Если не разу не обновляли
            link.createdAt(OffsetDateTime.now());
            link.lastUpdatedTime(gitHubResponse.updated());
            log.error("==UpdateLink :: handler первое заполнили время создания");
            return Optional.empty();
        }

        //произошло изменение
        if (!link.lastUpdatedTime().equals(gitHubResponse.updated())) {
            log.error("==UpdateLink :: handler отправили изменения");
            link.lastUpdatedTime(gitHubResponse.updated());
            return Optional.of(link);
        }
        //ничего не произошло
        return Optional.empty();
    }



//    @JsonProperty("question_id") long id,
//    String title,
//    @JsonProperty("is_answered") boolean isAnswered,
//    @JsonProperty("answer_count") long answerCount,
//    @JsonProperty("last_activity_date")
//    OffsetDateTime lastActivityDate,
//    @JsonProperty("creation_date") OffsetDateTime creationDate
//    ) {


        private Optional<Link> checkUpdateLinkStack(Link link, StackOverFlowResponse stackOverFlowResponse) {
        if (link.createdAt() == null) {
            //Если не разу не обновляли
            link.createdAt(OffsetDateTime.now());
            List<StackOverFlowResponse.ItemResponse> ss  = stackOverFlowResponse.items();

            link.lastUpdatedTime(stackOverFlowResponse.items().get(0).lastActivityDate());
            log.error("==UpdateLink :: checkUpdateLinkStack первое заполнили время создания");
            return Optional.empty();
        }

        //произошло изменение
        if (!link.lastUpdatedTime().equals(stackOverFlowResponse.items().get(0).lastActivityDate())) {
            log.error("==UpdateLink :: handler отправили изменения");
            link.lastUpdatedTime(stackOverFlowResponse.items().get(0).lastActivityDate());
            return Optional.of(link);
        }
        //ничего не произошло
        return Optional.empty();
    }

}
