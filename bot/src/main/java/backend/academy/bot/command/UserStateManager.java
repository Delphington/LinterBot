package backend.academy.bot.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class UserStateManager {

    @Getter
    @Setter
    @NoArgsConstructor
    private class InfoURI {
        private URI uri;
        private List<String> tags;
        private List<String> filters;
    }

    private final Map<Long, UserState> userStates = new ConcurrentHashMap<>();

    //Временное хранилище IRL, как только добавленные теги и фильтры, очищается
    private final Map<Long, InfoURI> useURIMap = new ConcurrentHashMap<>();


    public void clear(Long chatId) {
        userStates.remove(chatId);
    }

    public void clearUseURIMap(Long chatId) {
        useURIMap.remove(chatId);
    }


    public boolean createUser(Long id) {
        if (userStates.get(id) == null) {
            userStates.put(id, UserState.WAITING_COMMAND);
            useURIMap.put(id, new InfoURI());
            return true;
        }
        return false;
    }

    public UserState getUserState(Long id) {
        return userStates.get(id);
    }

    public void setUserStatus(Long id, UserState userState) {
        userStates.put(id, userState);
    }

    //-------------------------------------
    public void addUserURI(Long id, URI uri) {
        useURIMap.get(id).uri(uri);
    }

    public void addUserTags(Long id, List<String> tagsList) {
        useURIMap.get(id).tags(tagsList);
    }


    public void addUserFilters(Long id, List<String> filtersList) {
        useURIMap.get(id).filters(filtersList);
    }


    // ------------------------------------------
    public URI  getURIByUserId(Long userId){
        return useURIMap.get(userId).uri;
    }

    public List<String> getListTagsByUserId(Long userId){
        return useURIMap.get(userId).tags;
    }

    public List<String> getListFiltersByUserId(Long userId){
        return useURIMap.get(userId).filters;
    }
}
