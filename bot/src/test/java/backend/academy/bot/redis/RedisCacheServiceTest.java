package backend.academy.bot.redis;

import backend.academy.bot.api.dto.response.ListLinksResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisCacheServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private RedisCacheService redisCacheService;

    private final Long chatId = 12345L;
    private final ListLinksResponse testResponse = new ListLinksResponse(Collections.emptyList(), 0);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        redisCacheService = new RedisCacheService(redisTemplate);

    }

    @Test
    @DisplayName("Сохранение данных в кеш")
    void cacheLinks_shouldSaveDataToCache() {
        // Act
        redisCacheService.cacheLinks(chatId, testResponse);

        // Assert
        verify(valueOperations).set("bot:links:12345", testResponse);
    }

    @Test
    @DisplayName("Получение данных из кеша")
    void getCachedLinks_shouldReturnCachedData() {
        // Arrange
        when(valueOperations.get("bot:links:12345")).thenReturn(testResponse);

        // Act
        ListLinksResponse result = redisCacheService.getCachedLinks(chatId);

        // Assert
        assertEquals(testResponse, result);
        verify(valueOperations).get("bot:links:12345");
    }

    @Test
    @DisplayName("Получение null при отсутствии данных в кеше")
    void getCachedLinks_shouldReturnNullWhenCacheEmpty() {
        // Arrange
        when(valueOperations.get("bot:links:12345")).thenReturn(null);

        // Act
        ListLinksResponse result = redisCacheService.getCachedLinks(chatId);

        // Assert
        assertNull(result);
        verify(valueOperations).get("bot:links:12345");
    }

    @Test
    @DisplayName("Очистка кеша для конкретного chatId")
    void invalidateCache_shouldDeleteCacheForSpecificChatId() {
        redisCacheService.invalidateCache(chatId);
        Assertions.assertNull(redisCacheService.getCachedLinks(chatId));
    }
}
