package backend.academy.bot.api.controller;

import backend.academy.bot.api.dto.request.LinkUpdate;
import backend.academy.bot.notification.NotificationService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
public class UpdateController {

    private final NotificationService notificationService;

    @Operation(summary = "Отправить обновление")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Обновление обработано")})
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/updates")
    public void update(@RequestBody @Valid LinkUpdate linkUpdate) {
        log.info("Пришло обновление по ссылке");
        notificationService.sendMessage(linkUpdate);
    }

    @PostMapping("/public")
    @ResponseStatus(HttpStatus.OK)
    @Timed(value = "update.timer", histogram = true)  // Явное включение гистограммы
    public void update() {
        log.info("Пришло обновление по ссылке");
    }
}
