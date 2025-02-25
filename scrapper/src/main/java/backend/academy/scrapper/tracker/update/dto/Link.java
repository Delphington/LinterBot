package backend.academy.scrapper.tracker.update.dto;

import java.net.URI;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Link {
    private Long id; //id самой ссылки
    private URI url;
    private OffsetDateTime lastUpdatedTime;
    private OffsetDateTime createdAt;
}
