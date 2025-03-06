package backend.academy.scrapper.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StackOverFlowRequest {
    private String number;
    private String order;
    private String sort;
    private String site;
    private String filter;

    public StackOverFlowRequest(String number) {
        this(number, "desc", "activity", "stackoverflow", "withbody");
    }
}
