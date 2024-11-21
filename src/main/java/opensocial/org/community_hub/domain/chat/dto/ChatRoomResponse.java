package opensocial.org.community_hub.domain.chat.dto;

public class ChatRoomResponse {

    private Long roomId;
    private String name;

    public ChatRoomResponse() {
    }

    public ChatRoomResponse(Long roomId, String name) {
        this.roomId = roomId;
        this.name = name;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
