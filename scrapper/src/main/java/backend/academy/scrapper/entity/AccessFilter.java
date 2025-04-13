package backend.academy.scrapper.entity;

import backend.academy.scrapper.dto.request.filter.FilterRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Setter
@Entity
@Table(name = "access_filter")
public class AccessFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tg_chat_id", nullable = false)
    private TgChat tgChat;

    @Column(name = "filter", nullable = false)
    private String filter;

    public static AccessFilter create(TgChat tgChat, String filter) {
        AccessFilter accessFilter = new AccessFilter();
        accessFilter.tgChat = tgChat;
        accessFilter.filter = filter;
        return accessFilter;
    }
}
