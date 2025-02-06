package backend.academy.bot.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    //Айди и имя
    private Map<Long, String> users = new ConcurrentHashMap<>();

    public Optional<String> findById(Long id) {
        if (users.get(id) == null) {
            return Optional.empty();
        }
        return Optional.of(users.get(id));
    }


    public void save(Long id, String name) {
        if (findById(id).isPresent()) {
            System.err.println("User already is Registrated");
        } else {
            System.err.println("User is Registrated sussfully:" + id + "#" + name);
            users.put(id, name);
        }
    }

}
