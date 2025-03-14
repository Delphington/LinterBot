package backend.academy.scrapper.entity;

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
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tg_chat_linkS")
public class TgChatLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tg_chat_id", nullable = false)
    private TgChat tgChat;

    @ManyToOne
    @JoinColumn(name = "link_id", nullable = false)
    private Link link;

    public void setChat(TgChat tgChat) {
        this.tgChat = tgChat;
        if (tgChat != null) {
            tgChat.tgChatLinks().add(this);
        }
    }

    public void setLink(Link link) {
        this.link = link;
        if (link != null) {
            link.tgChatLinks().add(this);
        }
    }
}
