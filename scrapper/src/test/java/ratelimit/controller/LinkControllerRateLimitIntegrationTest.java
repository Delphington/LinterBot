package ratelimit.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ScrapperApplication.class)
@AutoConfigureMockMvc
public class LinkControllerRateLimitIntegrationTest implements RateLimitIntegration {
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        RateLimitTestDatabaseContainer.configureProperties(registry);
        RateLimitKafkaTestContainer.kafkaProperties(registry);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RateLimitProperties rateLimitProperties;
    private static final Long TG_CHAT_ID = 12345L;

    @Test
    @DisplayName("LinkController getAllLinks: Проверяем что с одного IP включается RateLimit")
    public void getAllLinks_testRateLimiting() throws Exception {
        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(get("/links")
                    .header("Tg-Chat-Id", TG_CHAT_ID)
                    .with(remoteAddr("192.168.3.1")))
                .andExpect(status().isOk());
        }

        mockMvc.perform(get("/links")
                .header("Tg-Chat-Id", TG_CHAT_ID)
                .with(remoteAddr("192.168.3.1")))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("LinkController getAllLinks: Проверяем что с разных IP не включается RateLimit")
    public void getAllLinks_testRateLimitingIP() throws Exception {
        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(get("/links")
                    .header("Tg-Chat-Id", TG_CHAT_ID)
                    .with(remoteAddr("192.168.3.2")))
                .andExpect(status().isOk());
        }

        mockMvc.perform(get("/links")
                .header("Tg-Chat-Id", TG_CHAT_ID)
                .with(remoteAddr("192.168.3.3")))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("LinkController addLink: Проверяем что с одного IP включается RateLimit")
    public void addLink_testRateLimiting() throws Exception {
        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(post("/links/" + TG_CHAT_ID)
                    .header("Tg-Chat-Id", TG_CHAT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                            {
                                "link": "https://github.com",
                                "tags": ["java", "spring"],
                                "filters": ["comments", "updates"]
                            }
                            """).with(remoteAddr("192.168.3.5")))
                .andExpect(status().isBadRequest());


        }
        mockMvc.perform(post("/links/" + TG_CHAT_ID)
                .header("Tg-Chat-Id", TG_CHAT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "link": "https://example.com",
                            "tags": ["java", "spring"],
                            "filters": ["comments", "updates"]
                        }
                        """).with(remoteAddr("192.168.3.5")))
            .andExpect(status().isTooManyRequests());
    }


    @Test
    @DisplayName("LinkController addLink: Проверяем что с разных IP не включается RateLimit")
    public void addLink_testRateLimitingIP() throws Exception {
        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(post("/links/" + TG_CHAT_ID)
                    .header("Tg-Chat-Id", TG_CHAT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                            {
                                "link": "https://github.com",
                                "tags": ["java", "spring"],
                                "filters": ["comments", "updates"]
                            }
                            """).with(remoteAddr("192.168.3.7")))
                .andExpect(status().isBadRequest());
        }

        mockMvc.perform(post("/links/" + TG_CHAT_ID)
                .header("Tg-Chat-Id", TG_CHAT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "link": "https://example.com",
                            "tags": ["java", "spring"],
                            "filters": ["comments", "updates"]
                        }
                        """).with(remoteAddr("192.168.3.200")))
            .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("LinkController deleteLink: Проверяем что с одного IP включается RateLimit")
    public void deleteLink_testRateLimiting() throws Exception {

        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(delete("/links/" + TG_CHAT_ID)
                    .header("Tg-Chat-Id", TG_CHAT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                            {
                                "link": "https://example.com"
                            }
                            """
                    ).with(remoteAddr("192.168.3.8")))
                .andExpect(status().isBadRequest());
        }

        mockMvc.perform(delete("/links/" + TG_CHAT_ID)
                .header("Tg-Chat-Id", TG_CHAT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "link": "https://example.com"
                        }
                        """
                ).with(remoteAddr("192.168.3.8")))
            .andExpect(status().isTooManyRequests());

    }

    @Test
    @DisplayName("LinkController deleteLink: Проверяем что с разных IP не включается RateLimit")
    public void deleteLink_testRateLimitingIP() throws Exception {
        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(delete("/links/" + TG_CHAT_ID)
                    .header("Tg-Chat-Id", TG_CHAT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                            {
                                "link": "https://github.com"
                            }
                            """
                    ).with(remoteAddr("192.168.3.9")))
                .andExpect(status().isBadRequest());

        }
        mockMvc.perform(delete("/links/" + TG_CHAT_ID)
                .header("Tg-Chat-Id", TG_CHAT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "link": "https://github.com"
                        }
                        """
                ).with(remoteAddr("192.168.3.10")))
            .andExpect(status().isBadRequest());
    }
}
