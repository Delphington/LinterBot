package datebase.service.jdbc;

import backend.academy.scrapper.dao.chat.TgChatDaoImpl;
import backend.academy.scrapper.exception.chat.ChatAlreadyExistsException;
import backend.academy.scrapper.exception.chat.ChatIllegalArgumentException;
import backend.academy.scrapper.exception.chat.ChatNotExistException;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.jdbc.JdbcChatService;
import datebase.service.TestDatabaseContainerService;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
        classes = {
            DataSourceAutoConfiguration.class,
            JdbcTemplateAutoConfiguration.class,
            TgChatDaoImpl.class,
            JdbcChatService.class
        })
@TestPropertySource(properties = {"app.database-access-type=jdbc", "spring.main.allow-bean-definition-overriding=true"})
@ActiveProfiles("jdbc")
public class JdbcTgChatServiceTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestDatabaseContainerService.configureProperties(registry);
    }

    @Autowired
    private ChatService chatService;

    private Long tgChatId;
    private Long linkId;

    @BeforeEach
    void setUp() {
        TestDatabaseContainerService.cleanDatabase();

        tgChatId = 1L;
        linkId = 1L;

        TestDatabaseContainerService.getJdbcTemplate()
                .update("INSERT INTO tg_chats (id, created_at) VALUES (?, NOW())", tgChatId);
        TestDatabaseContainerService.getJdbcTemplate()
                .update("INSERT INTO links (id, url, updated_at) VALUES (?, ?, NOW())", linkId, "https://example.com");
        TestDatabaseContainerService.getJdbcTemplate()
                .update("INSERT INTO tg_chat_links (tg_chat_id, link_id) VALUES (?, ?)", tgChatId, linkId);
    }

    @AfterEach
    void tearDown() {
        TestDatabaseContainerService.closeConnections();
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
