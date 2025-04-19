package controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.controller.TagController;
import backend.academy.scrapper.dto.request.tag.TagLinkRequest;
import backend.academy.scrapper.dto.request.tag.TagRemoveRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.dto.response.TagListResponse;
import backend.academy.scrapper.service.TagService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TagController.class)
@ContextConfiguration(classes = {TagController.class, BeanConfiguration.class})
public class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TagService tagService;

    private final Long testChatId = 123L;
    private final String testTag = "java";
    private final URI testUri = URI.create("https://example.com");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("GET /tag/{tgChatId} - успешное получение ссылок по тегу")
    void getListLinksByTag_shouldReturnOk() throws Exception {
        // given
        TagLinkRequest request = new TagLinkRequest(testTag);
        LinkResponse linkResponse = new LinkResponse(1L, testUri, List.of(testTag), List.of());
        ListLinksResponse expectedResponse = new ListLinksResponse(List.of(linkResponse), 1);

        when(tagService.getListLinkByTag(testChatId, testTag)).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/tag/{tgChatId}", testChatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links[0].id").value(1L))
                .andExpect(jsonPath("$.links[0].url").value(testUri.toString()))
                .andExpect(jsonPath("$.links[0].tags[0]").value(testTag))
                .andExpect(jsonPath("$.size").value(1));

        verify(tagService).getListLinkByTag(testChatId, testTag);
    }

    @Test
    @DisplayName("GET /tag/{tgChatId}/all - успешное получение всех тегов")
    void getAllListLinksByTag_shouldReturnOk() throws Exception {
        // given
        TagListResponse expectedResponse = new TagListResponse(List.of("java", "spring", "kotlin"));

        when(tagService.getAllListLinks(testChatId)).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/tag/{tgChatId}/all", testChatId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags.length()").value(3))
                .andExpect(jsonPath("$.tags[0]").value("java"))
                .andExpect(jsonPath("$.tags[1]").value("spring"))
                .andExpect(jsonPath("$.tags[2]").value("kotlin"));

        verify(tagService).getAllListLinks(testChatId);
    }

    @Test
    @DisplayName("DELETE /tag/{tgChatId} - успешное удаление тега из ссылки")
    void removeTagFromLink_shouldReturnOk() throws Exception {
        // given
        TagRemoveRequest request = new TagRemoveRequest(testTag, testUri);
        LinkResponse expectedResponse = new LinkResponse(1L, testUri, List.of(), List.of());

        when(tagService.removeTagFromLink(testChatId, request)).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(delete("/tag/{tgChatId}", testChatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.url").value(testUri.toString()))
                .andExpect(jsonPath("$.tags").isEmpty());

        verify(tagService).removeTagFromLink(testChatId, request);
    }
}
