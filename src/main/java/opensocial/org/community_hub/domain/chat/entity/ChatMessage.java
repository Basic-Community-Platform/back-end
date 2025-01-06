package opensocial.org.community_hub.domain.chat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import opensocial.org.community_hub.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @Column(name = "message_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Setter
    @Getter
    @Column(nullable = false)
    private String content;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // 외래키
    private User user;  // User 엔터티 참조

    @Setter
    @Getter
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    public ChatMessage() {}

    public ChatMessage(MessageType type, String content, User user, ChatRoom chatRoom) {
        this.type = type;
        this.content = content;
        this.user = user;
        this.chatRoom = chatRoom;
        this.timestamp = LocalDateTime.now();  // 메시지 생성 시 현재 시간으로 설정
    }
}
