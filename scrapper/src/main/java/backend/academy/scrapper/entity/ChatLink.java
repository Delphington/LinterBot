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
@Table(name = "tg_chat_link")
public class ChatLink {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tg_chat_id")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "link_id")
    private Link link;

    public void setChat(Chat chat) {
        this.chat = chat;
        if (chat != null) {
            chat.chatLinks().add(this);
        }
    }

    public void setLink(Link link) {
        this.link = link;
        if (link != null) {
            link.chatLinks().add(this);
        }
    }
}
