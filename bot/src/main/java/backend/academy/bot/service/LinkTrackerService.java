package backend.academy.bot.service;

import backend.academy.bot.exception.UserNotFoundException;
import lombok.Getter;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LinkTrackerService {

    //ID, List<URL>

    private Map<Long, List<String>> listLink = new ConcurrentHashMap<>();

    //Инцилизируется при /start
    public void createUser(Long id) {
        listLink.put(id, new ArrayList<>());
    }


    public Optional<List<String>> findAll(Long id) {
        checkCommand(id);
        List<String> link = listLink.get(id);
        return Optional.ofNullable(link);
    }

    public String createLink(Long id, String url) {
        checkCommand(id);

        if (findByLink(id, url)) {
            return "Url уже отслеживается";
        } else {
            listLink.get(id).add(url);
            return "Url добавлен";
        }
    }

    public String deleteLink(Long id, String url) {
        checkCommand(id);
        if (findByLink(id, url)) {
            listLink.get(id).remove(url);
            return "Url удален";
        } else {
            return "Такого Url нету в списке";
        }
    }





    //--------------------------------------
    private boolean findByLink(Long id, String url) {
        List<String> list = listLink.get(id);
        return list.stream().anyMatch(item -> item.equals(url));
    }


    private void checkCommand(Long id) {
        if (listLink.get(id) == null) {
            throw new UserNotFoundException("Команда была выполнена до регистрации пользователя");
        }
    }
}
