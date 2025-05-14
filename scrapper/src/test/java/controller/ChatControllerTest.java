package controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.controller.ChatController;
import backend.academy.scrapper.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChatController.class)
@ContextConfiguration(classes = {ChatController.class, BeanConfiguration.class})
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatService chatService;

    @Test
    @DisplayName("Успешная регистрация чата с валидным ID")
    void registerChat_validId_returnsOk() throws Exception {
        long validId = 1L;
        mockMvc.perform(post("/tg-chat/{id}", validId)).andExpect(status().isOk());
        verify(chatService).registerChat(validId);
    }

    @Test
    @DisplayName("Ошибка при регистрации с нечисловым ID")
    void registerChat_nonNumericId_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/tg-chat/abc")).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Успешное удаление чата с валидным ID")
    void deleteChat_validId_returnsOk() throws Exception {
        long validId = 1L;
        mockMvc.perform(delete("/tg-chat/{id}", validId)).andExpect(status().isOk());
        verify(chatService).deleteChat(validId);
    }

    @Test
    @DisplayName("Ошибка при удалении с нечисловым ID")
    void deleteChat_nonNumericId_returnsBadRequest() throws Exception {
        mockMvc.perform(delete("/tg-chat/abc")).andExpect(status().isBadRequest());
    }
}
