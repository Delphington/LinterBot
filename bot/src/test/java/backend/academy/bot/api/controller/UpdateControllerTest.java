package backend.academy.bot.api.controller;

import static org.mockito.Mockito.*;

import backend.academy.bot.api.dto.request.LinkUpdate;
import backend.academy.bot.executor.RequestExecutor;
import com.pengrad.telegrambot.request.SendMessage;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UpdateControllerTest {

    @Mock
    private RequestExecutor requestExecutor;

    @InjectMocks
    private UpdateController updateController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Обработка обновления: пустой список chatIds")
    void testUpdate_EmptyChatIds() throws Exception {
        // Arrange
        LinkUpdate linkUpdate =
                new LinkUpdate(1L, new URI("https://github.com/example"), "Новое обновление", List.of());

        // Act
        updateController.update(linkUpdate);

        // Assert
        verify(requestExecutor, never()).execute(any(SendMessage.class));
    }
}
