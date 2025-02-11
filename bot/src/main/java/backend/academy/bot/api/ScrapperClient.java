package backend.academy.bot.api;

import backend.academy.bot.api.dto.request.AddLinkRequest;
import backend.academy.bot.api.dto.response.LinkResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;






@Log4j2
@Service
public class ScrapperClient {
    private WebClient webClient;

    private String tgChatPath = "tg-chat/{id}";
    private String linkPath = "links/{tgChatId}";

    public ScrapperClient(
        WebClient.Builder webClientBuilder,
        @Value("${app.link.scrapper-uri}") String baseUrl
    ) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public void registerChat(Long id){
        log.error("====== FROM ScapperClient(tgbot) Registered id  = " + id);

        webClient.post()
            .uri(uriBuilder -> uriBuilder.path(tgChatPath).build(id))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> {
                return response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        String errorMessage = "Ошибка сервера регистрации: " + response.statusCode() + ", Body: " + errorBody;
                        log.error(errorMessage);
                        return Mono.error(new ResponseException(response.statusCode().toString()));
                    });
            })
            .bodyToMono(Void.class)
            .block();

    }

    //todo:
    //метод delete




    public LinkResponse trackLink(Long tgChatId, AddLinkRequest linkRequest){

        log.warn("МЫ в trackLink FROM ScapperClient");

        return webClient.post()
            .uri(uriBuilder -> uriBuilder.path(linkPath).build(tgChatId))
            .header("Tg-Chat-Id", String.valueOf(tgChatId)) // Add Tg-Chat-Id header
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(linkRequest), AddLinkRequest.class) // Отправляем AddLinkRequest в теле запроса.
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> {
                return response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        String errorMessage = "Ошибка добавления ссылки " + response.statusCode() + ", Body: " + errorBody;
                        log.error(errorMessage);
                        return Mono.error(new ResponseException(response.statusCode().toString()));
                    });
            })
            .bodyToMono(LinkResponse.class) // Читаем тело ответа и преобразуем его в LinkResponse.
            .block();
    }




}













//
//@Log4j2
//@Service
//public class ScrapperClient {
//    private RestClient restClient;
//
//    private String tgChatPath = "tg-chat/{id}";
//    //private String tgChatIdHeader = "Tg-Chat-Id";
//    private String linkPath = "links/{tgChatId}";
//
//
//    public ScrapperClient(
//        RestClient.Builder restClientBuilder,
//        @Value("${app.link.scrapper-uri}") String baseUrl
//    ) {
//        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
//    }
//
//
//    public void registerChat(Long id){
//        log.error("====== FROM ScapperClient(tgbot) Registared id  = " + id);
//
//        if (id == null) {
//            throw new IllegalArgumentException("Chat ID cannot be null");
//        }
//
//        restClient.post()
//            .uri(tgChatPath, id)
//            .retrieve() //Отправляет запрос
//            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
//                String errorMessage = "Ошибка сервера регистрации: " + response.getStatusCode();
//                log.error(errorMessage);
//                throw new ResponseException(response.getStatusCode().toString());
//            })
//            .toBodilessEntity(); //получаем только заголовки
//
//    }
// //метод delete
//
//    public LinkResponse trackLink(Long tgChatId, AddLinkRequest linkRequest){
//        return
//            restClient.post()
//                .uri(linkPath, tgChatId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(Mono.just(linkRequest), AddLinkRequest.class)
//                .
//    }
//
//}
