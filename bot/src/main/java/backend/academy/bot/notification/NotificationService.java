package backend.academy.bot.notification;

import backend.academy.bot.api.dto.request.LinkUpdate;
import backend.academy.bot.redis.RedisMessageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationProperties properties;
    private final MessageUpdateSender messageUpdateSender;
    private final RedisMessageService redisMessageService;

    public void sendMessage(LinkUpdate linkUpdate) {
        if (properties.mode() == NotificationMode.IMMEDIATE) {
            messageUpdateSender.sendMessage(linkUpdate);
        } else {
            redisMessageService.addCacheLinks(linkUpdate);
        }
    }

    @Scheduled(cron = "${app.notification.daily-digest-cron}")
    public void sendDailyDigest() {
        if (properties.mode() != NotificationMode.DAILY_DIGEST) {
            return;
        }
        List<LinkUpdate> updates = redisMessageService.getCachedLinks();
        if (updates != null && !updates.isEmpty()) {
            updates.forEach(messageUpdateSender::sendMessage);
            redisMessageService.invalidateCache();
        }
    }
}
