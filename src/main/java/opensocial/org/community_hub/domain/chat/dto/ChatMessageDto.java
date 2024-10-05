package opensocial.org.community_hub.domain.chat.dto;

public class ChatMessageDto {

    private String type;
    private String sender;
    private String content;
    private String roomId;

    public ChatMessageDto() {}

    public ChatMessageDto(String type, String sender, String content, String roomId) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.roomId = roomId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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
