//
// class LinkUpdaterSchedulerTest {
//    @Mock
//    private UpdaterLinks updaterLinks;
//
//    @InjectMocks
//    private LinkUpdaterScheduler linkUpdaterScheduler;
//
//    private ThreadPoolTaskScheduler taskScheduler;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        // Создаем и запускаем планировщик
//        taskScheduler = new ThreadPoolTaskScheduler();
//        taskScheduler.initialize();
//        taskScheduler.scheduleAtFixedRate(
//            linkUpdaterScheduler::update,
//            TimeUnit.SECONDS.toMillis(1) // Интервал 1 секунда
//        );
//    }
//
//    @Test
//    @DisplayName("Проверка вызова метода updateLink с заданным интервалом")
//    void testUpdateLinkCalledWithInterval() throws InterruptedException {
//        // Ждем 3 секунды
//        Thread.sleep(3000);
//
//        // Проверяем, что метод updateLink вызывался два раза
//        verify(updaterLinks, times(4)).updateLink();
//    }
// }
