package backend.academy.bot.api.controller;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import backend.academy.bot.api.dto.request.LinkUpdate;
import backend.academy.bot.notification.NotificationService;
import java.net.URI;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ExtendWith(MockitoExtension.class)
public class UpdateControllerTest {

    @Mock
    private NotificationService notificationService;

    private UpdateController updateController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        updateController = new UpdateController(notificationService);
    }

    @Test
    @DisplayName("Успешная обработка обновления ссылки")
    void update_ShouldProcessValidUpdate() {
        // Arrange
        LinkUpdate linkUpdate = new LinkUpdate(123L,  URI.create("https://www.example.com"), "Some description", Collections.emptyList());


        doNothing().when(notificationService).sendMessage(linkUpdate);

        // Act & Assert
        assertDoesNotThrow(() -> updateController.update(linkUpdate));
        verify(notificationService, times(1)).sendMessage(linkUpdate);
    }

    @Test
    @DisplayName("Проверка аннотаций контроллера")
    void controller_ShouldHaveCorrectAnnotations() {
        // Проверяем аннотации класса
        assertNotNull(UpdateController.class.getAnnotation(RestController.class));

        // Проверяем аннотации метода
        try {
            var method = UpdateController.class.getMethod("update", LinkUpdate.class);
            assertNotNull(method.getAnnotation(PostMapping.class));
            assertEquals("/updates", method.getAnnotation(PostMapping.class).value()[0]);
            assertNotNull(method.getAnnotation(ResponseStatus.class));
            assertEquals(HttpStatus.OK, method.getAnnotation(ResponseStatus.class).value());
        } catch (NoSuchMethodException e) {
            fail("Метод update не найден");
        }
    }
}
