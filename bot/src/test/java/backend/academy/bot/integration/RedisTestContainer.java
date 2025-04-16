package backend.academy.bot.integration;

import backend.academy.bot.api.dto.request.LinkUpdate;
import java.util.List;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisTestContainer {
    private static final DockerImageName REDIS_IMAGE = DockerImageName.parse("redis:7.4.2");
    private static final int REDIS_PORT = 6379;
    private static GenericContainer<?> redisContainer;

    public static void startContainer() {
        if (redisContainer == null) {
            redisContainer = new GenericContainer<>(REDIS_IMAGE).withExposedPorts(REDIS_PORT);
            redisContainer.start();
        }
    }

    public static void stopContainer() {
        if (redisContainer != null && redisContainer.isRunning()) {
            redisContainer.stop();
        }
    }

    public static <T> RedisTemplate<String, T> createRedisTemplate(Class<T> valueType) {
        if (redisContainer == null || !redisContainer.isRunning()) {
            throw new IllegalStateException("Redis container is not running");
        }

        RedisStandaloneConfiguration config =
                new RedisStandaloneConfiguration(redisContainer.getHost(), redisContainer.getMappedPort(REDIS_PORT));

        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(config);
        connectionFactory.afterPropertiesSet();

        RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    public static RedisTemplate<String, List<LinkUpdate>> createRedisTemplateList() {
        if (redisContainer == null || !redisContainer.isRunning()) {
            throw new IllegalStateException("Redis container is not running");
        }

        RedisStandaloneConfiguration config =
                new RedisStandaloneConfiguration(redisContainer.getHost(), redisContainer.getMappedPort(REDIS_PORT));

        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(config);
        connectionFactory.afterPropertiesSet();

        RedisTemplate<String, List<LinkUpdate>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    public static void flushAll(RedisTemplate<?, ?> redisTemplate) {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }
}
