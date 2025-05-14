package backend.academy.bot.redis;

import backend.academy.bot.api.dto.request.LinkUpdate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisMessageService {

    private static final String KEY_DIGEST = "bot:notifications";
    private final RedisTemplate<String, List<LinkUpdate>> redisTemplate;
    private static final long TTL_HOURS = 24; // Срок хранения

    public void addCacheLinks(LinkUpdate linkUpdate) {
        synchronized (this) {
            List<LinkUpdate> currentList = redisTemplate.opsForValue().get(KEY_DIGEST);
            if (currentList == null) {
                currentList = new ArrayList<>();
            }
            currentList.add(linkUpdate);
            redisTemplate.opsForValue().set(KEY_DIGEST, currentList);
        }
    }

    public List<LinkUpdate> getCachedLinks() {
        return redisTemplate.opsForValue().get(KEY_DIGEST);
    }

    public void invalidateCache() {
        redisTemplate.delete(KEY_DIGEST);
    }
}
