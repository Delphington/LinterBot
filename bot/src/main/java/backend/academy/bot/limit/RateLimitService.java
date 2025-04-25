package backend.academy.bot.limit;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    //IP - ключ
    private final Map<String, Bucket> ipBuckets;
    private final Bandwidth bandwidth;

    public boolean tryConsume(String clientIp) {
        Bucket bucket = ipBuckets.computeIfAbsent(clientIp, k -> Bucket.builder()
            .addLimit(bandwidth)
            .build());

        return bucket.tryConsume(1);
    }
}
