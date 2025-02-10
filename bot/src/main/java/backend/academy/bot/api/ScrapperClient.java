package backend.academy.bot.api;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Log4j2
@Service
public class ScrapperClient {
    private RestClient restClient;

    private String tgChatPath = "tg-chat/{id}";
    private String tgChatIdHeader = "Tg-Chat-Id";
    private String linkPath = "links";


    public ScrapperClient(
        RestClient.Builder restClientBuilder,
        @Value("${app.link.scrapper-uri}") String baseUrl
    ) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }


    public void registerChat(Long id){

        if (id == null) {
            throw new IllegalArgumentException("Chat ID cannot be null");
        }
        //todo: написать на ошибки 500
        restClient.post()
            .uri(tgChatPath, id)
            .retrieve() //Отправляет запрос
            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                String errorMessage = "Ошибка сервера регистрации: " + response.getStatusCode();
                log.error(errorMessage);
                throw new ResponseException(response.getStatusCode().toString());
            })
            .toBodilessEntity();

    }


    public void deleteChat(Long id){
        restClient.delete()
            .uri(tgChatPath, id)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                throw new ResponseException(response.getStatusCode().toString());
            })
            .toBodilessEntity();
    }


    //==============================================================
    //============= Link ===========================================
    //==============================================================

}
