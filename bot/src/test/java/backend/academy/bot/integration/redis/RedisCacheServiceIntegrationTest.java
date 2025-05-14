package backend.academy.bot.integration.redis;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.bot.api.dto.response.LinkResponse;
import backend.academy.bot.api.dto.response.ListLinksResponse;
import backend.academy.bot.integration.RedisTestContainer;
import backend.academy.bot.redis.RedisCacheService;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DisplayName("Тесты RedisCacheService с Testcontainers")
public class RedisCacheServiceIntegrationTest {

    private RedisTemplate<String, Object> redisTemplate;
    private RedisCacheService redisCacheService;

    @BeforeAll
    static void beforeAll() {
        RedisTestContainer.startContainer();
    }

    @BeforeEach
    void setUp() {
        redisTemplate = RedisTestContainer.createRedisTemplate(Object.class);
        redisCacheService = new RedisCacheService(redisTemplate);
        RedisTestContainer.flushAll(redisTemplate);
    }

    @Test
    @DisplayName("Сохранение и получение данных из кеша")
    void cacheAndGetLinks_ShouldWorkCorrectly() {
        // Arrange
        Long chatId = 12345L;
        ListLinksResponse expectedResponse = new ListLinksResponse(
                List.of(
                        new LinkResponse(
                                1L, URI.create("https://github.com"), Collections.emptyList(), Collections.emptyList()),
                        new LinkResponse(
                                2L,
                                URI.create("https://stackoverflow.com"),
                                Collections.emptyList(),
                                Collections.emptyList())),
                2);

        // Act
        redisCacheService.cacheLinks(chatId, expectedResponse);
        ListLinksResponse actualResponse = redisCacheService.getCachedLinks(chatId);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.links().size(), actualResponse.links().size());
        assertEquals(
                expectedResponse.links().get(0).url(),
                actualResponse.links().get(0).url());
    }

    @Test
    @DisplayName("Получение данных при отсутствии в кеше")
    void getCachedLinks_WhenNotCached_ShouldReturnNull() {
        // Arrange
        Long chatId = 54321L;

        // Act
        ListLinksResponse response = redisCacheService.getCachedLinks(chatId);

        // Assert
        assertNull(response);
    }

    @Test
    @DisplayName("Инвалидация кеша")
    void invalidateCache_ShouldRemoveData() {
        // Arrange
        Long chatId = 11111L;
        ListLinksResponse response = new ListLinksResponse(
                List.of(new LinkResponse(
                        1L, URI.create("https://example.com"), Collections.emptyList(), Collections.emptyList())),
                1);
        redisCacheService.cacheLinks(chatId, response);

        // Act
        redisCacheService.invalidateCache(chatId);
        ListLinksResponse afterInvalidation = redisCacheService.getCachedLinks(chatId);

        // Assert
        assertNull(afterInvalidation);
    }
}
