package backend.academy.bot.redis;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import backend.academy.bot.api.dto.request.LinkUpdate;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class RedisMessageServiceTest {

    @Mock
    private RedisTemplate<String, List<LinkUpdate>> redisTemplate;

    @Mock
    private ValueOperations<String, List<LinkUpdate>> valueOperations;

    private RedisMessageService redisMessageService;

    private final LinkUpdate linkUpdate1 =
        new LinkUpdate(1L, URI.create("https://github.com"), "desc1", new ArrayList<>());
    private final LinkUpdate linkUpdate2 =
        new LinkUpdate(2L, URI.create("https://github.com"), "desc2", new ArrayList<>());

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        redisMessageService = new RedisMessageService(redisTemplate);
    }

    @Test
    @DisplayName("Добавление ссылки в пустой кеш")
    void addCacheLinks_shouldAddNewLinkToEmptyCache() {
        // Arrange
        when(valueOperations.get(anyString())).thenReturn(null);

        // Act
        redisMessageService.addCacheLinks(linkUpdate1);

        // Assert
        verify(valueOperations).get("bot:notifications");
        verify(valueOperations).set(eq("bot:notifications"), argThat(list ->
            list != null && list.size() == 1 && list.get(0).equals(linkUpdate1)
        ));
    }

    @Test
    @DisplayName("Добавление ссылки в существующий кеш")
    void addCacheLinks_shouldAddNewLinkToExistingCache() {
        // Arrange
        List<LinkUpdate> existingList = new ArrayList<>(List.of(linkUpdate1));
        when(valueOperations.get(anyString())).thenReturn(existingList);

        // Act
        redisMessageService.addCacheLinks(linkUpdate2);

        // Assert
        verify(valueOperations).get("bot:notifications");
        verify(valueOperations).set(eq("bot:notifications"), argThat(list ->
            list != null && list.size() == 2 &&
            list.contains(linkUpdate1) && list.contains(linkUpdate2)
        ));
    }

    @Test
    @DisplayName("Получение данных из кеша")
    void getCachedLinks_shouldReturnCachedLinks() {
        // Arrange
        List<LinkUpdate> expectedList = Arrays.asList(linkUpdate1, linkUpdate2);
        when(valueOperations.get("bot:notifications")).thenReturn(expectedList);

        // Act
        List<LinkUpdate> result = redisMessageService.getCachedLinks();

        // Assert
        assertEquals(expectedList, result);
        verify(valueOperations).get("bot:notifications");
    }

    @Test
    @DisplayName("Получение null при пустом кеше")
    void getCachedLinks_shouldReturnNullWhenCacheEmpty() {
        // Arrange
        when(valueOperations.get("bot:notifications")).thenReturn(null);

        // Act
        List<LinkUpdate> result = redisMessageService.getCachedLinks();

        // Assert
        assertNull(result);
        verify(valueOperations).get("bot:notifications");
    }

    @Test
    @DisplayName("Очистка кеша")
    void invalidateCache_shouldDeleteKey() {
        // Act
        redisMessageService.invalidateCache();

        // Assert
        verify(redisTemplate).delete("bot:notifications");
    }
}
