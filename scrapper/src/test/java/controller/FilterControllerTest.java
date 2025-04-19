package controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.controller.FilterController;
import backend.academy.scrapper.dto.request.filter.FilterRequest;
import backend.academy.scrapper.dto.response.filter.FilterListResponse;
import backend.academy.scrapper.dto.response.filter.FilterResponse;
import backend.academy.scrapper.service.AccessFilterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FilterController.class)
@ContextConfiguration(classes = {FilterController.class, BeanConfiguration.class})
public class FilterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccessFilterService accessFilterService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("POST /filter/{tgChatId}/create - успешное создание фильтра")
    void createFilter_ShouldReturnCreated() throws Exception {
        Long tgChatId = 123L;
        FilterRequest request = new FilterRequest("test filter");
        FilterResponse expectedResponse = new FilterResponse(1L, "test filter");

        when(accessFilterService.createFilter(tgChatId, request)).thenReturn(expectedResponse);

        mockMvc.perform(post("/filter/{tgChatId}/create", tgChatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.filter").value("test filter"));
    }

    @Test
    @DisplayName("GET /filter/{tgChatId} - успешное получение списка фильтров")
    void getAllFilter_ShouldReturnFilterList() throws Exception {
        Long tgChatId = 123L;
        List<FilterResponse> filters = List.of(new FilterResponse(1L, "filter1"), new FilterResponse(2L, "filter2"));
        FilterListResponse expectedResponse = new FilterListResponse(filters);

        when(accessFilterService.getAllFilter(tgChatId)).thenReturn(expectedResponse);

        mockMvc.perform(get("/filter/{tgChatId}", tgChatId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filterList.length()").value(2))
                .andExpect(jsonPath("$.filterList[0].id").value(1L))
                .andExpect(jsonPath("$.filterList[0].filter").value("filter1"))
                .andExpect(jsonPath("$.filterList[1].id").value(2L))
                .andExpect(jsonPath("$.filterList[1].filter").value("filter2"));
    }

    @Test
    @DisplayName("DELETE /filter/{tgChatId}/delete - успешное удаление фильтра")
    void deleteFilter_ShouldReturnOk() throws Exception {
        Long tgChatId = 123L;
        FilterRequest request = new FilterRequest("filter to delete");
        FilterResponse expectedResponse = new FilterResponse(1L, "filter to delete");

        when(accessFilterService.deleteFilter(tgChatId, request)).thenReturn(expectedResponse);

        mockMvc.perform(delete("/filter/{tgChatId}/delete", tgChatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.filter").value("filter to delete"));
    }

    @Test
    @DisplayName("POST /filter/{tgChatId}/create - валидация: фильтр слишком длинный")
    void createFilter_ShouldReturnBadRequestWhenFilterTooLong() throws Exception {
        Long tgChatId = 123L;
        String longFilter = "a".repeat(51);
        FilterRequest request = new FilterRequest(longFilter);

        mockMvc.perform(post("/filter/{tgChatId}/create", tgChatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("POST /filter/{tgChatId}/create - валидация: фильтр пустой")
    void createFilter_ShouldReturnBadRequestWhenFilterEmpty() throws Exception {
        Long tgChatId = 123L;
        FilterRequest request = new FilterRequest("");

        mockMvc.perform(post("/filter/{tgChatId}/create", tgChatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful());
    }
}
