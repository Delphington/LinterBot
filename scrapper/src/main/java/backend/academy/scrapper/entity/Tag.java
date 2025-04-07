package backend.academy.scrapper.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "link_id", nullable = false, referencedColumnName = "id")
    private Link link;

    @Column(name = "tag", nullable = false)
    private String tag;

    // Фабричный метод
    public static Tag create(String tagName, Link link) {
        Tag tag = new Tag();
        tag.tag(tagName);
        tag.link(link);
        return tag;
    }

    // Фабричный метод
    public static Tag create(Long id ,String tagName) {
        Tag tag = new Tag();
        tag.id = id;
        tag.tag(tagName);
        return tag;
    }
}
