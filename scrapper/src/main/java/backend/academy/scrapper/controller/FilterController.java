package backend.academy.scrapper.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/filter")
@Slf4j
@RequiredArgsConstructor
public class FilterController {


    @PostMapping("/${tgChatId}/create")
    public void createFilter(){

    }



    @DeleteMapping("/${tgChatId}/delete")
    public void deleteFilter(){

    }

    @GetMapping("/${tgChatId}")
    public void getAllFilter(){

    }

}
