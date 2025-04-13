package backend.academy.bot.redis;

import backend.academy.bot.api.dto.response.ListLinksResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private static final String CACHE_PREFIX = "bot:links";
    private final RedisTemplate<String, Object> redisTemplate;

    public void cacheLinks(Long chatId, ListLinksResponse response) {
        redisTemplate.opsForValue().set(buildKey(chatId), response);
    }

    public ListLinksResponse getCachedLinks(Long chatId) {
        return (ListLinksResponse) redisTemplate.opsForValue().get(buildKey(chatId));
    }

    public void invalidateCache(Long chatId) {
        redisTemplate.delete(buildKey(chatId));
    }

    private String buildKey(Long chatId) {
        return CACHE_PREFIX + ":" + chatId;
    }
}
