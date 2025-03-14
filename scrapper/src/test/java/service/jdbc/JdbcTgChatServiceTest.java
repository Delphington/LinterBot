package service.jdbc;

import backend.academy.scrapper.exception.chat.ChatAlreadyExistsException;
import backend.academy.scrapper.exception.chat.ChatIllegalArgumentException;
import backend.academy.scrapper.exception.chat.ChatNotExistException;
import backend.academy.scrapper.service.jdbc.JdbcChatService;
import base.IntegrationTest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class JdbcTgChatServiceTest extends IntegrationTest {

    @Autowired
    private JdbcChatService jdbcChatService;


    @Test
    @Transactional
    public void registerChatTest() {
        jdbcChatService.registerChat(100L);
        Assert.assertThrows(ChatAlreadyExistsException.class, () -> {
            jdbcChatService.registerChat(100L);
        });

        Assert.assertThrows(ChatIllegalArgumentException.class, () -> {
            jdbcChatService.registerChat(null);
        });

        Assert.assertThrows(ChatIllegalArgumentException.class, () -> {
            jdbcChatService.registerChat(0L);
        });

        Assert.assertThrows(ChatIllegalArgumentException.class, () -> {
            jdbcChatService.registerChat(-1L);
        });

    }

    @Test
    @Transactional
    public void deleteChatTest() {
        Assert.assertThrows(ChatNotExistException.class, () -> {
            jdbcChatService.deleteChat(100L);
            ;
        });

        //-----------
        jdbcChatService.registerChat(1000L);
        jdbcChatService.deleteChat(1000L);

        Assert.assertThrows(ChatNotExistException.class, () -> {
            jdbcChatService.deleteChat(100L);
        });

    }
}
