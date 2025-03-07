package backend.academy.scrapper.api.service;

import backend.academy.scrapper.api.entity.Link;
import backend.academy.scrapper.api.repository.ChatLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatLinkService {

    private final ChatLinkRepository chatLinkRepository;

    public List<Link> findLinksByChatId(Long chatId) {
        return chatLinkRepository.findLinksByChatId(chatId);
    }

}
