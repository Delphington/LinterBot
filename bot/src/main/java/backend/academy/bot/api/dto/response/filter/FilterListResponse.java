package backend.academy.bot.api.dto.response.filter;

import lombok.ToString;
import java.util.List;

@ToString
public class FilterListResponse {
    private Long id;
    private List<String> filterList;
}
