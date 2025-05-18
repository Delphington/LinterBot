package metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.scrapper.client.TgBotClient;
import backend.academy.scrapper.repository.TgChatLinkRepository;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.tracker.client.GitHubClient;
import backend.academy.scrapper.tracker.client.StackOverFlowClient;
import backend.academy.scrapper.tracker.update.LinkUpdateProcessor;
import backend.academy.scrapper.tracker.update.dto.LinkDto;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class LinkUpdateProcessorMetricsTest {
    @Configuration
    static class TestMetricsConfig {
        @Bean
        public MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }

        @Bean
        public AtomicInteger githubProcessedLinksCounter() {
            return new AtomicInteger(0);
        }

        @Bean
        public AtomicInteger stackoverflowProcessedLinksCounter() {
            return new AtomicInteger(0);
        }

        @Bean
        public Timer githubScrapeTimer(MeterRegistry registry) {
            return Timer.builder("scrapper.scrape.time")
                    .description("Time taken to scrape GitHub links")
                    .register(registry);
        }

        @Bean
        public Timer stackoverflowScrapeTimer(MeterRegistry registry) {
            return Timer.builder("scrapper.scrape.time")
                    .description("Time taken to scrape StackOverflow links")
                    .register(registry);
        }
    }

    private MeterRegistry meterRegistry;
    private AtomicInteger githubCounter;
    private AtomicInteger stackoverflowCounter;
    private Timer githubTimer;
    private Timer stackoverflowTimer;
    private LinkUpdateProcessor linkUpdateProcessor;

    @Mock
    private TgBotClient tgBotClient;

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private StackOverFlowClient stackOverFlowClient;

    @Mock
    private LinkService linkService;

    @Mock
    private TgChatLinkRepository tgChatLinkRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализируем тестовые бины
        TestMetricsConfig config = new TestMetricsConfig();
        meterRegistry = config.meterRegistry();
        githubCounter = config.githubProcessedLinksCounter();
        stackoverflowCounter = config.stackoverflowProcessedLinksCounter();
        githubTimer = config.githubScrapeTimer(meterRegistry);
        stackoverflowTimer = config.stackoverflowScrapeTimer(meterRegistry);

        // Создаем тестируемый процессор с моками и реальными метриками
        linkUpdateProcessor = new LinkUpdateProcessor(
                tgBotClient,
                gitHubClient,
                stackOverFlowClient,
                linkService,
                tgChatLinkRepository,
                githubCounter,
                stackoverflowCounter,
                githubTimer,
                stackoverflowTimer);
    }

    @Test
    void testGithubScrapeTimer() {
        // Проверяем начальное состояние таймера
        assertEquals(0, githubTimer.count());

        // Имитируем обработку GitHub ссылки
        linkUpdateProcessor.handlerUpdateGitHub(
                new LinkDto(1L, URI.create("https://github.com/user/repo"), OffsetDateTime.now(), null));

        // Проверяем, что таймер зарегистрировал вызов
        assertEquals(1, githubTimer.count());
        assertTrue(githubTimer.totalTime(TimeUnit.MILLISECONDS) > 0);
    }

    @Test
    void testStackoverflowScrapeTimer() {
        // Проверяем начальное состояние таймера
        assertEquals(0, stackoverflowTimer.count());

        // Имитируем обработку StackOverflow ссылки
        linkUpdateProcessor.handlerUpdateStackOverFlow(
                new LinkDto(1L, URI.create("https://stackoverflow.com/questions/123"), OffsetDateTime.now(), null));

        // Проверяем, что таймер зарегистрировал вызов
        assertEquals(1, stackoverflowTimer.count());
        assertTrue(stackoverflowTimer.totalTime(TimeUnit.MILLISECONDS) > 0);
    }
}
