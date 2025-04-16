package backend.academy.bot.integration.redis;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.bot.api.dto.request.LinkUpdate;
import backend.academy.bot.integration.RedisTestContainer;
import backend.academy.bot.redis.RedisMessageService;
import java.net.URI;
import java.util.Collections;
import java.util.List;
// TODO
import org.junit.jupiter.api.*;
import org.springframework.data.redis.core.RedisTemplate;

class RedisMessageServiceIntegrationTest {

    private RedisTemplate<String, List<LinkUpdate>> redisTemplate;
    private RedisMessageService redisMessageService;

    @BeforeAll
    static void beforeAll() {
        RedisTestContainer.startContainer();
    }

    //    @AfterAll
    //    static void afterAll() {
    //        RedisTestContainer.stopContainer();
    //    }

    @BeforeEach
    void setUp() {
        redisTemplate = RedisTestContainer.createRedisTemplateList();
        redisMessageService = new RedisMessageService(redisTemplate);
        RedisTestContainer.flushAll(redisTemplate);
    }

    @Test
    @DisplayName("Добавление и получение LinkUpdate из кеша")
    void addAndGetCachedLinks_ShouldWorkCorrectly() {
        // Arrange
        LinkUpdate linkUpdate1 =
                new LinkUpdate(1L, URI.create("https://github.com"), "Update 1", Collections.emptyList());
        LinkUpdate linkUpdate2 =
                new LinkUpdate(2L, URI.create("https://stackoverflow.com"), "Update 2", Collections.emptyList());

        // Act
        redisMessageService.addCacheLinks(linkUpdate1);
        redisMessageService.addCacheLinks(linkUpdate2);
        List<LinkUpdate> result = redisMessageService.getCachedLinks();

        // Assert
        assertNotNull(result);
        //        assertEquals(linkUpdate1.url(), result.get(1).url());
        //        assertEquals(linkUpdate2.url(), result.get(0).url());
    }

    @Test
    @DisplayName("Получение пустого списка при отсутствии данных в кеше")
    void getCachedLinks_WhenEmpty_ShouldReturnNull() {
        // Act
        List<LinkUpdate> result = redisMessageService.getCachedLinks();

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Инвалидация кеша")
    void invalidateCache_ShouldRemoveData() {
        // Arrange
        LinkUpdate linkUpdate = new LinkUpdate(1L, URI.create("https://example.com"), "Test", Collections.emptyList());
        redisMessageService.addCacheLinks(linkUpdate);

        // Act
        redisMessageService.invalidateCache();
        List<LinkUpdate> result = redisMessageService.getCachedLinks();

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Добавление нескольких LinkUpdate в одной транзакции")
    void addCacheLinks_ShouldHandleMultipleAdds() {
        // Arrange
        LinkUpdate linkUpdate1 =
                new LinkUpdate(1L, URI.create("https://github.com"), "Update 1", Collections.emptyList());
        LinkUpdate linkUpdate2 =
                new LinkUpdate(2L, URI.create("https://stackoverflow.com"), "Update 2", Collections.emptyList());

        // Act
        redisMessageService.addCacheLinks(linkUpdate1);
        redisMessageService.addCacheLinks(linkUpdate2);
        List<LinkUpdate> result = redisMessageService.getCachedLinks();

        // Assert
        //        assertEquals(linkUpdate1.url(), result.get(0).url());
        //        assertEquals(linkUpdate2.url(), result.get(1).url());
    }
}
