package opensocial.org.community_hub.domain.chat.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    public ChatMessage() {}

    public ChatMessage(MessageType type, String content, String sender, ChatRoom chatRoom) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.chatRoom = chatRoom;
        this.timestamp = LocalDateTime.now();  // 메시지 생성 시 현재 시간으로 설정
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
