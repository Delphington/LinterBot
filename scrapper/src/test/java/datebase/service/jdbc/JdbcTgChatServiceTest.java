package datebase.service.jdbc;

import backend.academy.scrapper.dao.chat.TgChatDaoImpl;
import backend.academy.scrapper.exception.chat.ChatAlreadyExistsException;
import backend.academy.scrapper.exception.chat.ChatIllegalArgumentException;
import backend.academy.scrapper.exception.chat.ChatNotExistException;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.jdbc.JdbcChatService;
import datebase.TestDatabaseContainer;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = {
    DataSourceAutoConfiguration.class,
    JdbcTemplateAutoConfiguration.class,
    TgChatDaoImpl.class,
    JdbcChatService.class
})
@TestPropertySource(properties = {
    "app.database-access-type=jdbc",
    "spring.main.allow-bean-definition-overriding=true"
})
@ActiveProfiles("jdbc")
public class JdbcTgChatServiceTest{

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestDatabaseContainer.configureProperties(registry);
    }

    @Autowired
    private ChatService chatService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long tgChatId;
    private Long linkId;

    @BeforeEach
    void setUp() {
        TestDatabaseContainer.cleanDatabase();

        tgChatId = 1L;
        linkId = 1L;

        jdbcTemplate.update("INSERT INTO tg_chats (id, created_at) VALUES (?, NOW())", tgChatId);
        jdbcTemplate.update(
            "INSERT INTO links (id, url, updated_at) VALUES (?, ?, NOW())",
            linkId, "https://example.com");
        jdbcTemplate.update(
            "INSERT INTO tg_chat_links (tg_chat_id, link_id) VALUES (?, ?)",
            tgChatId, linkId);
    }


    @Test
    @DisplayName("Создание чата")
    public void registerChatTest() {
        chatService.registerChat(100L);
        Assert.assertThrows(ChatAlreadyExistsException.class, () -> {
            chatService.registerChat(100L);
        });

        Assert.assertThrows(ChatIllegalArgumentException.class, () -> {
            chatService.registerChat(null);
        });

        Assert.assertThrows(ChatIllegalArgumentException.class, () -> {
            chatService.registerChat(0L);
        });

        Assert.assertThrows(ChatIllegalArgumentException.class, () -> {
            chatService.registerChat(-1L);
        });
    }

    @Test
    @DisplayName("Удаления чата")
    public void deleteChatTest() {
        Assert.assertThrows(ChatNotExistException.class, () -> {
            chatService.deleteChat(100L);
        });

        chatService.registerChat(1000L);
        chatService.deleteChat(1000L);

        Assert.assertThrows(ChatNotExistException.class, () -> {
            chatService.deleteChat(100L);
        });
    }
}
