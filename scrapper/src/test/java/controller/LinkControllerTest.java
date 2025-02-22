package controller;

import backend.academy.scrapper.api.controller.LinkController;
import backend.academy.scrapper.api.dto.request.AddLinkRequest;
import backend.academy.scrapper.api.dto.request.RemoveLinkRequest;
import backend.academy.scrapper.api.dto.response.LinkResponse;
import backend.academy.scrapper.api.dto.response.ListLinksResponse;
import backend.academy.scrapper.api.service.LinkService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.net.URI;
import java.util.Collections;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(LinkController.class)
@ContextConfiguration(classes = {LinkController.class, LinkControllerTest.TestConfig.class})
@AutoConfigureMockMvc
public class LinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkService linkService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public LinkService linkService() {
            return Mockito.mock(LinkService.class);
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("Получение всех link с помощью getAllLinks ")
    public void link_getAll_whenTgChatIdIsValid() {
        ListLinksResponse mockResponse = new ListLinksResponse(Collections.emptyList(), 0);
        when(linkService.getAllLinks(1L)).thenReturn(mockResponse);

        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/links")
                .header("Tg-Chat-Id", "1")
        ).andExpect(status().isOk());
    }


    @SneakyThrows
    @Test
    @DisplayName("Добавление ссылки по tg-chat-id")
    public void link_addLink_whenTgChatIdIsValid() {
        AddLinkRequest addLinkRequest = new AddLinkRequest(
            URI.create("http://localhost"),
            Collections.emptyList(),
            Collections.emptyList()
        );

        LinkResponse mockLinkResponse = new LinkResponse(
            2L,
            URI.create("http://localhost"),
            Collections.emptyList(),
            Collections.emptyList()
        );

        when(linkService.addLink(1L, addLinkRequest)).thenReturn(mockLinkResponse);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/links/{tgChatId}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addLinkRequest))
                    .header("Tg-Chat-Id", "1")
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(2L)) // Проверяем ID в ответе
            .andExpect(jsonPath("$.url").value("http://localhost")); // Проверяем URL в ответе

        Mockito.verify(linkService).addLink(1L, addLinkRequest);
    }


    @SneakyThrows
    @Test
    @DisplayName("Удаление ссылки по tg-chat-id")
    public void link_deleteLink_whenTgChatIdIsValid() {
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(
            URI.create("http://localhost")
        );

        LinkResponse mockLinkResponse = new LinkResponse(
            2L,
            URI.create("http://localhost"),
            Collections.emptyList(),
            Collections.emptyList()
        );

        when(linkService.deleteLink(1L, removeLinkRequest.link())).thenReturn(mockLinkResponse);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .delete("/links/{tgChatId}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(removeLinkRequest))
                    .header("Tg-Chat-Id", "1")
            ).andExpect(status().isOk());

        Mockito.verify(linkService).deleteLink(1L, removeLinkRequest.link());
    }


}
