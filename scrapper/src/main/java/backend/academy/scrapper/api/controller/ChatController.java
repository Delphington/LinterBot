package backend.academy.scrapper.api.controller;

import backend.academy.scrapper.api.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/tg-chat")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "Зарегистрировать чат")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Чат зарегистрирован")})
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}")
    public void registerChat(@PathVariable Long id) {
        log.info("ChatController registerChat {} ", sanitize(id));
        chatService.registerChat(id);
    }

    @Operation(summary = "Удалить чат")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Чат успешно удалён")})
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteChat(@PathVariable Long id) {
        log.info("ChatController deleteChat {} ", sanitize(id));
        chatService.deleteChat(id);
    }

    private String sanitize(Long id) {
        return String.valueOf(id).replace("\r", "").replace("\n", "");
    }
}
