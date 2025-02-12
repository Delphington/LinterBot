package backend.academy.scrapper.client.tracker;

import backend.academy.scrapper.api.dto.request.LinkUpdatesRequest;
import backend.academy.scrapper.client.bot.TelegramBotClient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class UpdateLinkService {

    private final TelegramBotClient telegramBotClient;

    List<Long> ids = new ArrayList<>();

    public void addUser(Long id) {
        ids.add(id);
    }

    public void updateLink() {
        if (ids.isEmpty()) return;
        List<Long> temp = new ArrayList<>();
        temp.add(21L);
        LinkUpdatesRequest lsls = new LinkUpdatesRequest(ids.get(0),
            URI.create("https://github.com/Delphington/SpringProjects"),
            "ПЕРЕДАЛ ЦЕЛОЕ СООБЩЕНИЕ",
            temp);

        telegramBotClient.addUpdate(lsls);
    }
}
