package backend.academy.scrapper.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "link")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "tags", columnDefinition = "TEXT[]")
    private List<String> tags = new ArrayList<>();

    @Column(name = "filters", columnDefinition = "TEXT[]")
    private List<String> filters = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "link", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ChatLink> chatLinks = new ArrayList<>();

    @Column(name = "description")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
