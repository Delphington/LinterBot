package backend.academy.bot.api.controller;

import backend.academy.bot.api.dto.request.LinkUpdate;
import backend.academy.bot.executor.RequestExecutor;
import com.pengrad.telegrambot.request.SendMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Log4j2
@RestController
public class UpdateController {

    private final RequestExecutor execute;

    @Operation(summary = "Отправить обновление")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Обновление обработано"
        )
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/updates")
    public void update(@RequestBody @Valid LinkUpdate updateRequest) {
        log.error("================================================");
        log.error("==UpdateController получили updateRequest");


        for (Long chatId : updateRequest.tgChatIds()) {
            SendMessage sendMessage = new SendMessage(
                chatId,
                String.format("Обновление по ссылке: %s\n описание: %s", updateRequest.url(), updateRequest.description())
            );
            execute.execute(sendMessage);

        }


    }
}
