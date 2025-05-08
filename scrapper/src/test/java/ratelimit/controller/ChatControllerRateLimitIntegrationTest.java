package ratelimit.controller;

import backend.academy.scrapper.ScrapperApplication;
import backend.academy.scrapper.limit.RateLimitProperties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ratelimit.RateLimitKafkaTestContainer;
import ratelimit.RateLimitTestDatabaseContainer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ScrapperApplication.class)
@AutoConfigureMockMvc
public class ChatControllerRateLimitIntegrationTest implements RateLimitIntegration {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        RateLimitTestDatabaseContainer.configureProperties(registry);
        RateLimitKafkaTestContainer.kafkaProperties(registry);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RateLimitProperties rateLimitProperties;

    @Test
    @DisplayName("ChatController register: Проверяем что с одного IP включается RateLimit")
    public void registerChat_testRateLimiting() throws Exception {
        mockMvc.perform(post("/tg-chat/123").with(remoteAddr("193.168.2.1")))
            .andExpect(status().isOk());
        for(int i=0; i< rateLimitProperties.capacity()-1; i++){
            mockMvc.perform(post("/tg-chat/123").with(remoteAddr("193.168.2.1")))
                .andExpect(status().isBadRequest());
        }
        mockMvc.perform(post("/tg-chat/123").with(remoteAddr("193.168.2.1")))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    @SneakyThrows
    @DisplayName("ChatController register: Проверяем что с разных IP не включается RateLimit")
    public void registerChat_testRateLimitingIP() {
        mockMvc.perform(post("/tg-chat/1236").with(remoteAddr("193.168.1.1")))
            .andExpect(status().isOk());
        for(int i=0; i< rateLimitProperties.capacity()-1; i++) {
            mockMvc.perform(post("/tg-chat/1236")
                    .with(remoteAddr("192.168.1.1")))
                .andExpect(status().isBadRequest());
        }

        mockMvc.perform(post("/tg-chat/1236").with(request -> {
            request.setRemoteAddr("192.168.1.5");
            return request;
        })).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("ChatController deleteChat: Проверяем что с одного IP включается RateLimit")
    public void deleteChat_testRateLimiting() throws Exception {
        for(int i=0; i< rateLimitProperties.capacity(); i++) {
            mockMvc.perform(delete("/tg-chat/55")
                    .with(remoteAddr("192.168.1.10")))
                .andExpect(status().isOk());
        }
        mockMvc.perform(delete("/tg-chat/55")
                .with(remoteAddr("192.168.1.10")))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    @SneakyThrows
    @DisplayName("ChatController deleteChat: Проверяем что с разных IP не включается RateLimit")
    public void deleteChat_testRateLimitingIP() {
        for(int i=0; i< rateLimitProperties.capacity(); i++) {
            mockMvc.perform(delete("/tg-chat/55")
                    .with(remoteAddr("192.168.1.11")))
                .andExpect(status().isOk());
        }
        mockMvc.perform(delete("/tg-chat/55")
            .with(request -> {
                request.setRemoteAddr("192.168.1.15");
                return request;
            })).andExpect(status().isOk());
    }
}
