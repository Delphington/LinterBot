package backend.academy.scrapper.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/tg-chat")
public class ChatController {

    @Operation(summary = "Зарегистрировать чат")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Чат зарегистрирован"
        )}
    )
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}")
    public void registerChat(@PathVariable Long id) {
        log.info("FROM ChatController registerChat id = " + id);
    }


    @Operation(summary = "Удалить чат")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Чат успешно удалён"
        )
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteChat(@PathVariable Long id) {
        log.info("FROM ChatController deleteChat id = " + id);
    }
}
