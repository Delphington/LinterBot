package backend.academy.scrapper.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "tg_chat")
@Builder
@NoArgsConstructor
public class Chat {

    @Id
    @Column(name = "id")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @ToString.Exclude
    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ChatLink> chatLinks = new ArrayList<>();
}
