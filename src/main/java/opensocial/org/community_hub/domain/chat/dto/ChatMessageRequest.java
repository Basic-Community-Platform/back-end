package opensocial.org.community_hub.domain.chat.dto;

import opensocial.org.community_hub.domain.chat.entity.ChatMessage.MessageType;

public class ChatMessageRequest {

    private MessageType type;
    private String content;
    private Long userId;  // user 객체가 아닌 userId를 받아 처리
    private Long roomId;  // chatRoom 객체 대신 roomId만 받음

    // Getters and Setters
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
}
