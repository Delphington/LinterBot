 package service.orm;

 import static org.junit.jupiter.api.Assertions.*;

 import backend.academy.scrapper.entity.TgChat;
 import backend.academy.scrapper.exception.chat.ChatAlreadyExistsException;
 import backend.academy.scrapper.repository.TgChatRepository;
 import backend.academy.scrapper.service.orm.OrmChatService;
 import base.IntegrationTest;
 import java.time.OffsetDateTime;
 import java.time.ZoneId;
 import java.util.Optional;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;
 import org.springframework.beans.factory.annotation.Autowired;

 class OrmChatServiceTest extends IntegrationTest {

    @Autowired
    private OrmChatService ormChatService;

    @Autowired
    private TgChatRepository tgChatRepository;

    private final Long tgChatId = 1L;

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом
        tgChatRepository.deleteAll();
    }

    @Test
    void registerChat_ShouldRegisterChat() {
        // Act
        ormChatService.registerChat(tgChatId);

        // Assert
        Optional<TgChat> tgChat = tgChatRepository.findById(tgChatId);
        assertTrue(tgChat.isPresent());
        assertEquals(tgChatId, tgChat.get().id());
    }

    @Test
    void registerChat_ShouldThrowChatAlreadyExistsException_WhenChatAlreadyExists() {
        // Arrange
        TgChat tgChat = TgChat.builder()
                .id(tgChatId)
                .createdAt(OffsetDateTime.now(ZoneId.systemDefault()))
                .build();
        tgChatRepository.save(tgChat);

        // Act & Assert
        assertThrows(ChatAlreadyExistsException.class, () -> ormChatService.registerChat(tgChatId));
    }

    @Test
    void findChatById_ShouldReturnChat_WhenChatExists() {
        // Arrange
        TgChat tgChat = TgChat.builder()
                .id(tgChatId)
                .createdAt(OffsetDateTime.now(ZoneId.systemDefault()))
                .build();
        tgChatRepository.save(tgChat);

        // Act
        Optional<TgChat> foundChat = ormChatService.findChatById(tgChatId);

        // Assert
        assertTrue(foundChat.isPresent());
        assertEquals(tgChatId, foundChat.get().id());
    }

    @Test
    void findChatById_ShouldReturnEmptyOptional_WhenChatDoesNotExist() {
        // Act
        Optional<TgChat> foundChat = ormChatService.findChatById(tgChatId);

        // Assert
        assertFalse(foundChat.isPresent());
    }
 }
