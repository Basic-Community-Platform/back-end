package opensocial.org.community_hub.domain.chat.dto;

import opensocial.org.community_hub.domain.user.entity.User;

public class ChatMessageDto {

    private String type;
    private User user;
    private String content;
    private String roomId;

    public ChatMessageDto() {}

    public ChatMessageDto(String type, User user, String content, String roomId) {
        this.type = type;
        this.user = user;
        this.content = content;
        this.roomId = roomId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
