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
public final class UserStateManager {

    @Getter
    @Setter
    @NoArgsConstructor
    private class InfoLink {
        private URI uri;
        private List<String> tags;
        private List<String> filters;
    }

    private final Map<Long, UserState> userStates = new ConcurrentHashMap<>();

    //Временное хранилище ID:InfoLink, как только добавленные теги и фильтры, очищается
    private final Map<Long, InfoLink> userInfoLinkMap = new ConcurrentHashMap<>();


    public boolean createUserIfNotExist(Long id) {
        if (userStates.get(id) == null) {
            userStates.put(id, UserState.WAITING_COMMAND);
            userInfoLinkMap.put(id, new InfoLink());
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
        userInfoLinkMap.get(id).uri(uri);
    }

    public void addUserTags(Long id, List<String> tagsList) {
        userInfoLinkMap.get(id).tags(tagsList);
    }

    public void addUserFilters(Long id, List<String> filtersList) {
        userInfoLinkMap.get(id).filters(filtersList);
    }

    // ------------------------------------------
    public URI getURIByUserId(Long userId) {
        return userInfoLinkMap.get(userId).uri;
    }

    public List<String> getListTagsByUserId(Long userId) {
        return userInfoLinkMap.get(userId).tags;
    }

    public List<String> getListFiltersByUserId(Long userId) {
        return userInfoLinkMap.get(userId).filters;
    }

    //-------------------------------------------
    public void clearUserStates(Long chatId) {
        userStates.remove(chatId);
    }

    public void clearUserInfoLinkMap(Long chatId) {
        userInfoLinkMap.remove(chatId);
    }


}
