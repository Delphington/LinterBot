package controller;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.controller.ChatController;
import backend.academy.scrapper.service.orm.OrmChatService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(ChatController.class)
@ContextConfiguration(classes = {ChatController.class, TgChatControllerTest.TestConfig.class})
@AutoConfigureMockMvc
public class TgChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrmChatService chatService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public OrmChatService chatService() {
            return Mockito.mock(OrmChatService.class);
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("ChatController регистрация чата при правильном id > 0")
    public void registerChat_whenChatIdIsValid_chatRegisteredSuccessfully() {
        mockMvc.perform(MockMvcRequestBuilders.post("/tg-chat/1")).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    @DisplayName("ChatController ошибка регистрации, если id не число")
    public void registerChat_whenChatIdIsNotValid_chatRegisteredNoSuccessfully() {
        mockMvc.perform(MockMvcRequestBuilders.post("/tg-chat/ss")).andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------

    @SneakyThrows
    @Test
    @DisplayName("ChatController удаление чата при правильном id > 0")
    public void deleteChat_whenChatIdIsValid_chatDeletedSuccessfully() {
        doNothing().when(chatService).deleteChat(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/tg-chat/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    @DisplayName("ChatController удаление чата с невалидным id <= 0")
    public void deleteChat_whenChatIdIsInvalid_throwsException() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/tg-chat/something").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
