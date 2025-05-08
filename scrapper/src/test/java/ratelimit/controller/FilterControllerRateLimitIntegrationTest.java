package ratelimit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.ScrapperApplication;
import backend.academy.scrapper.limit.RateLimitProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ratelimit.RateLimitKafkaTestContainer;
import ratelimit.RateLimitTestDatabaseContainer;

@SpringBootTest(classes = ScrapperApplication.class)
@AutoConfigureMockMvc
public class FilterControllerRateLimitIntegrationTest implements RateLimitIntegration {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        RateLimitTestDatabaseContainer.configureProperties(registry);
        RateLimitKafkaTestContainer.kafkaProperties(registry);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RateLimitProperties rateLimitProperties;

    private static final Long TG_CHAT_ID = 54321L;
    private static final String TEST_FILTER = "test-filter";

    @Test
    @DisplayName("FilterController createFilter: Проверяем что с одного IP включается RateLimit")
    public void createFilter_testRateLimiting() throws Exception {
        // Имитируем несколько запросов до достижения лимита
        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(post("/filter/" + TG_CHAT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"filter\": \"" + TEST_FILTER + i + "\"}")
                            .with(remoteAddr("192.168.4.1")))
                    .andExpect(status().isBadRequest());
        }

        // Проверяем, что следующий запрос получает TooManyRequests
        mockMvc.perform(post("/filter/" + TG_CHAT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"filter\": \"overflow-filter\"}")
                        .with(remoteAddr("192.168.4.1")))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("FilterController createFilter: Проверяем что с разных IP не включается RateLimit")
    public void createFilter_testRateLimitingIP() throws Exception {
        // Заполняем лимит для первого IP
        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(post("/filter/" + TG_CHAT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"filter\": \"" + TEST_FILTER + i + "\"}")
                            .with(remoteAddr("192.168.4.2")))
                    .andExpect(status().isBadRequest());
        }

        // Проверяем, что с другого IP запросы проходят
        mockMvc.perform(post("/filter/" + TG_CHAT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"filter\": \"another-ip-filter\"}")
                        .with(remoteAddr("192.168.4.3")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("FilterController getAllFilter: Проверяем что с одного IP включается RateLimit")
    public void getAllFilter_testRateLimiting() throws Exception {
        // Имитируем несколько запросов до достижения лимита
        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(get("/filter/" + TG_CHAT_ID).with(remoteAddr("192.168.4.4")))
                    .andExpect(status().isBadRequest());
        }

        // Проверяем, что следующий запрос получает TooManyRequests
        mockMvc.perform(get("/filter/" + TG_CHAT_ID).with(remoteAddr("192.168.4.4")))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("FilterController getAllFilter: Проверяем что с разных IP не включается RateLimit")
    public void getAllFilter_testRateLimitingIP() throws Exception {
        // Заполняем лимит для первого IP
        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(get("/filter/" + TG_CHAT_ID).with(remoteAddr("192.168.4.5")))
                    .andExpect(status().isBadRequest());
        }

        // Проверяем, что с другого IP запросы проходят
        mockMvc.perform(get("/filter/" + TG_CHAT_ID).with(remoteAddr("192.168.4.6")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("FilterController deleteFilter: Проверяем что с одного IP включается RateLimit")
    public void deleteFilter_testRateLimiting() throws Exception {
        // Имитируем несколько запросов до достижения лимита
        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(delete("/filter/" + TG_CHAT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"filter\": \"" + TEST_FILTER + i + "\"}")
                            .with(remoteAddr("192.168.4.7")))
                    .andExpect(status().isBadRequest());
        }

        // Проверяем, что следующий запрос получает TooManyRequests
        mockMvc.perform(delete("/filter/" + TG_CHAT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"filter\": \"overflow-filter\"}")
                        .with(remoteAddr("192.168.4.7")))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("FilterController deleteFilter: Проверяем что с разных IP не включается RateLimit")
    public void deleteFilter_testRateLimitingIP() throws Exception {
        // Заполняем лимит для первого IP
        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(delete("/filter/" + TG_CHAT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"filter\": \"" + TEST_FILTER + i + "\"}")
                            .with(remoteAddr("192.168.4.8")))
                    .andExpect(status().isBadRequest());
        }

        // Проверяем, что с другого IP запросы проходят
        mockMvc.perform(delete("/filter/" + TG_CHAT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"filter\": \"another-ip-filter\"}")
                        .with(remoteAddr("192.168.4.9")))
                .andExpect(status().isBadRequest());
    }
}
