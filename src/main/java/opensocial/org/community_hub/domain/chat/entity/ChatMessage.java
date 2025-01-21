package opensocial.org.community_hub.domain.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import opensocial.org.community_hub.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // 외래키
    private User user;  // User 엔터티 참조

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    public ChatMessage(MessageType type, String content, User user, ChatRoom chatRoom) {
        this.type = type;
        this.content = content;
        this.user = user;
        this.chatRoom = chatRoom;
        this.timestamp = LocalDateTime.now(); // 생성 시점의 시간 자동 설정
    }

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
