package backend.academy.scrapper.tracker.update.dto;

import lombok.*;

import java.net.URI;
import java.time.OffsetDateTime;

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
