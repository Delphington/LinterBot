package backend.academy.bot.redis;

import backend.academy.bot.api.dto.request.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisMessageService {

    private static final String KEY_DIGEST = "bot:notifications";
    private final RedisTemplate<String, List<LinkUpdate>> redisTemplate;
    private static final long TTL_HOURS = 24; // Срок хранения
    public void addCacheLinks(LinkUpdate linkUpdate) {
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public <K, V> List<Object> execute(RedisOperations<K, V> operations) throws DataAccessException {
                operations.multi(); // Начало транзакции

                List<LinkUpdate> currentList = redisTemplate.opsForValue().get(KEY_DIGEST);
                List<LinkUpdate> newList = currentList != null ?
                    new ArrayList<>(currentList) : new ArrayList<>();

                newList.add(linkUpdate);
                redisTemplate.opsForValue().set(KEY_DIGEST, newList);
                redisTemplate.expire(KEY_DIGEST, TTL_HOURS, TimeUnit.HOURS);

                return operations.exec(); // Фиксация транзакции
            }
        });
    }


    public List<LinkUpdate> getCachedLinks() {
        return redisTemplate.opsForValue().get(KEY_DIGEST);
    }

    public void invalidateCache() {
        redisTemplate.delete(KEY_DIGEST);
    }
}
