package backend.academy.scrapper.controller;

import backend.academy.scrapper.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/tg-chat/tags")
public class TagController {

    private final TagService tagService;

}
