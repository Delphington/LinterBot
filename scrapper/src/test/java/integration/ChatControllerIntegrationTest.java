package integration;


import backend.academy.scrapper.api.controller.ChatController;
import backend.academy.scrapper.api.exception.handler.ChatHandlerException;
import backend.academy.scrapper.api.service.ChatService;
import backend.academy.scrapper.api.service.LinkService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ContextConfiguration(classes = {ChatController.class, ChatService.class,
    ChatControllerIntegrationTest.TestConfig.class, ChatHandlerException.class})
@AutoConfigureMockMvc
public class ChatControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatService chatService;

    @Autowired
    private LinkService linkService;


    @TestConfiguration
    static class TestConfig {
        @Bean
        public LinkService linkService() {
            return Mockito.mock(LinkService.class);
        }
    }


    @Test
    @DisplayName("Регистрация чата с валидным ID")
    @SneakyThrows
    public void registerChat_whenChatIdIsValid_chatRegisteredSuccessfully() {
        mockMvc.perform(
            post("/tg-chat/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }


    @Test
    @DisplayName("Регистрация чата с невалидным ID <= 0")
    @SneakyThrows
    public void registerChat_whenChatIdIsInvalid_throwsException() {
        mockMvc.perform(
            post("/tg-chat/{id}", 0L)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Регистрация повторная регистрация ID <= 0")
    @SneakyThrows
    public void registerChat_whenChatIdIsInvalidAndExist_throwsException() {
        mockMvc.perform(
            post("/tg-chat/{id}", 10L)
                .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
            post("/tg-chat/{id}", 10L)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Удаление чата с валидным ID")
    @SneakyThrows
    public void deleteChat_whenChatIdIsValid_chatDeletedSuccessfully(){
        chatService.registerChat(2L);
        // Выполняем запрос на удаление чата
        mockMvc.perform(
            delete("/tg-chat/{id}", 2L)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Удаление чата с невалидным ID <= 0")
    @SneakyThrows
    public void deleteChat_whenChatIdIsInvalid_throwsException() {
        // Выполняем запрос на удаление чата с невалидным ID
        mockMvc.perform(
            delete("/tg-chat/{id}", 0L)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Удаление несуществующего чата")
    @SneakyThrows
    public void deleteChat_whenChatDoesNotExist_throwsException() {
        // Выполняем запрос на удаление несуществующего чата
        mockMvc.perform(
            delete("/tg-chat/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }
}
