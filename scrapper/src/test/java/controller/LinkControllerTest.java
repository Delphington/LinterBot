package controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.controller.LinkController;
import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.dto.request.RemoveLinkRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.service.LinkService;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LinkController.class)
@ContextConfiguration(classes = {LinkController.class, BeanConfiguration.class})
public class LinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkService linkService;

    private final Long testChatId = 123L;
    private final URI testUrl = URI.create("https://example.com");
    private final List<String> testTags = List.of("java", "spring");
    private final List<String> testFilters = List.of("comments", "updates");

    @Test
    @DisplayName("Получение всех ссылок - успешный сценарий")
    void getAllLinks_shouldReturnOk() throws Exception {
        LinkResponse linkResponse = new LinkResponse(1L, testUrl, testTags, testFilters);
        ListLinksResponse expectedResponse = new ListLinksResponse(List.of(linkResponse), 1);

        when(linkService.findAllLinksByChatId(testChatId)).thenReturn(expectedResponse);

        mockMvc.perform(get("/links").header("Tg-Chat-Id", testChatId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links[0].id").value(1L))
                .andExpect(jsonPath("$.links[0].url").value(testUrl.toString()))
                .andExpect(jsonPath("$.links[0].tags").isArray())
                .andExpect(jsonPath("$.links[0].tags[0]").value("java"))
                .andExpect(jsonPath("$.links[0].filters[1]").value("updates"))
                .andExpect(jsonPath("$.size").value(1));

        verify(linkService).findAllLinksByChatId(testChatId);
    }

    @Test
    @DisplayName("Добавление ссылки с тегами и фильтрами - успешный сценарий")
    void addLink_withTagsAndFilters_shouldReturnOk() throws Exception {
        AddLinkRequest request = new AddLinkRequest(testUrl, testTags, testFilters);
        LinkResponse expectedResponse = new LinkResponse(1L, testUrl, testTags, testFilters);

        when(linkService.addLink(testChatId, request)).thenReturn(expectedResponse);

        mockMvc.perform(
                        post("/links/{tgChatId}", testChatId)
                                .header("Tg-Chat-Id", testChatId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                    {
                        "link": "https://example.com",
                        "tags": ["java", "spring"],
                        "filters": ["comments", "updates"]
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.url").value(testUrl.toString()))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags[1]").value("spring"))
                .andExpect(jsonPath("$.filters[0]").value("comments"));

        verify(linkService).addLink(testChatId, request);
    }

    @Test
    @DisplayName("Добавление ссылки без тегов и фильтров - успешный сценарий")
    void addLink_withoutOptionalFields_shouldReturnOk() throws Exception {
        AddLinkRequest request = new AddLinkRequest(testUrl, null, null);
        LinkResponse expectedResponse = new LinkResponse(1L, testUrl, null, null);

        when(linkService.addLink(testChatId, request)).thenReturn(expectedResponse);

        mockMvc.perform(
                        post("/links/{tgChatId}", testChatId)
                                .header("Tg-Chat-Id", testChatId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                    {
                        "link": "https://example.com"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.url").value(testUrl.toString()))
                .andExpect(jsonPath("$.tags").doesNotExist())
                .andExpect(jsonPath("$.filters").doesNotExist());
    }

    @Test
    @DisplayName("Удаление ссылки - успешный сценарий")
    void deleteLink_shouldReturnOk() throws Exception {
        RemoveLinkRequest request = new RemoveLinkRequest(testUrl);
        LinkResponse expectedResponse = new LinkResponse(1L, testUrl, testTags, testFilters);

        when(linkService.deleteLink(testChatId, request.link())).thenReturn(expectedResponse);

        mockMvc.perform(
                        delete("/links/{tgChatId}", testChatId)
                                .header("Tg-Chat-Id", testChatId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                    {
                        "link": "https://example.com"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.url").value(testUrl.toString()))
                .andExpect(jsonPath("$.tags[0]").value("java"))
                .andExpect(jsonPath("$.filters[1]").value("updates"));

        verify(linkService).deleteLink(testChatId, request.link());
    }
}
