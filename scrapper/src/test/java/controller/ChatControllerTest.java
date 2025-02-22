package controller;

import backend.academy.scrapper.api.controller.ChatController;
import backend.academy.scrapper.api.service.ChatService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@ContextConfiguration(classes = {ChatController.class, ChatControllerTest.TestConfig.class})
@AutoConfigureMockMvc
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatService chatService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ChatService chatService() {
            return Mockito.mock(ChatService.class);
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("ChatController регистрация чата при правильном id > 0")
    public void registerChat_whenChatIdIsValid_chatRegisteredSuccessfully() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/tg-chat/1")
        ).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    @DisplayName("ChatController ошибка регистрации, если id не число")
    public void registerChat_whenChatIdIsNotValid_chatRegisteredNoSuccessfully() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/tg-chat/ss")
        ).andExpect(status().isBadRequest());
    }

    //------------------------------------------------------------------

    @SneakyThrows
    @Test
    @DisplayName("ChatController удаление чата при правильном id > 0")
    public void deleteChat_whenChatIdIsValid_chatDeletedSuccessfully() {
        doNothing().when(chatService).deleteChat(1L);

        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/tg-chat/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    @DisplayName("ChatController удаление чата с невалидным id <= 0")
    public void deleteChat_whenChatIdIsInvalid_throwsException() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/tg-chat/something")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }
}
