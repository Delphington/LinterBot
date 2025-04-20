package backend.academy.scrapper.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "tg_chats")
@Builder
@NoArgsConstructor
public class TgChat {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "tgChat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TgChatLink> tgChatLinks = new ArrayList<>(); // Явная инициализация;

    @OneToMany(mappedBy = "tgChat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccessFilter> accessFilters = new ArrayList<>(); // Явная инициализация;
}
