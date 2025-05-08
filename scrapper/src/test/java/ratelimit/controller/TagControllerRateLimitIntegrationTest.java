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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ScrapperApplication.class)
@AutoConfigureMockMvc
public class TagControllerRateLimitIntegrationTest implements RateLimitIntegration {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        RateLimitTestDatabaseContainer.configureProperties(registry);
        RateLimitKafkaTestContainer.kafkaProperties(registry);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RateLimitProperties rateLimitProperties;

    private static final Long TG_CHAT_ID = 67890L;
    private static final String TEST_TAG = "test-tag";
    private static final String TEST_URI = "https://example.com";


    @Test
    @DisplayName("TagController getListLinksByTag: Проверяем что с одного IP включается RateLimit")
    public void getListLinksByTag_testRateLimiting() throws Exception {
        // Имитируем несколько запросов до достижения лимита
        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(get("/tag/" + TG_CHAT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                            {
                              "tag" : "tag1"
                            }
                            """
                    )
                    .with(remoteAddr("192.168.5.1")))
                .andExpect(status().isOk());
        }

        // Проверяем превышение лимита
        mockMvc.perform(get("/tag/" + TG_CHAT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                          "tag" : "tag1"
                        }
                        """
                )
                .with(remoteAddr("192.168.5.1")))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("TagController getListLinksByTag: Проверяем что с разных IP не включается RateLimit")
    public void getListLinksByTag_testRateLimitingIP() throws Exception {
        // Заполняем лимит для первого IP
        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(get("/tag/" + TG_CHAT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "tag" : "tag1"
                        }
                        """
                    )                    .with(remoteAddr("192.168.5.2")))
                .andExpect(status().isOk());
        }

        // Проверяем запрос с другого IP
        mockMvc.perform(get("/tag/" + TG_CHAT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "tag" : "tag1"
                    }
                    """
                )                .with(remoteAddr("192.168.5.3")))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("TagController getAllListLinksByTag: Проверяем что с одного IP включается RateLimit")
    public void getAllListLinksByTag_testRateLimiting() throws Exception {
        // Имитируем несколько запросов до достижения лимита
        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(get("/tag/" + TG_CHAT_ID + "/all")
                    .with(remoteAddr("192.168.5.4")))
                .andExpect(status().isOk());
        }

        // Проверяем превышение лимита
        mockMvc.perform(get("/tag/" + TG_CHAT_ID + "/all")
                .with(remoteAddr("192.168.5.4")))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("TagController getAllListLinksByTag: Проверяем что с разных IP не включается RateLimit")
    public void getAllListLinksByTag_testRateLimitingIP() throws Exception {
        // Заполняем лимит для первого IP
        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(get("/tag/" + TG_CHAT_ID + "/all")
                    .with(remoteAddr("192.168.5.5")))
                .andExpect(status().isOk());
        }

        // Проверяем запрос с другого IP
        mockMvc.perform(get("/tag/" + TG_CHAT_ID + "/all")
                .with(remoteAddr("192.168.5.6")))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("TagController removeTagFromLink: Проверяем что с одного IP включается RateLimit")
    public void removeTagFromLink_testRateLimiting() throws Exception {
        // Имитируем несколько запросов до достижения лимита
        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(delete("/tag/" + TG_CHAT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"tag\": \"" + TEST_TAG + i + "\", \"uri\": \"" + TEST_URI + i + "\"}")
                    .with(remoteAddr("192.168.5.7")))
                .andExpect(status().isBadRequest());
        }

        // Проверяем превышение лимита
        mockMvc.perform(delete("/tag/" + TG_CHAT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tag\": \"overflow-tag\", \"uri\": \"https://overflow.com\"}")
                .with(remoteAddr("192.168.5.7")))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("TagController removeTagFromLink: Проверяем что с разных IP не включается RateLimit")
    public void removeTagFromLink_testRateLimitingIP() throws Exception {
        // Заполняем лимит для первого IP
        for (int i = 0; i < rateLimitProperties.capacity(); i++) {
            mockMvc.perform(delete("/tag/" + TG_CHAT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"tag\": \"" + TEST_TAG + i + "\", \"uri\": \"" + TEST_URI + i + "\"}")
                    .with(remoteAddr("192.168.5.8")))
                .andExpect(status().isBadRequest());
        }

        // Проверяем запрос с другого IP
        mockMvc.perform(delete("/tag/" + TG_CHAT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tag\": \"another-ip-tag\", \"uri\": \"https://another.com\"}")
                .with(remoteAddr("192.168.5.9")))
            .andExpect(status().isBadRequest());
    }
}
