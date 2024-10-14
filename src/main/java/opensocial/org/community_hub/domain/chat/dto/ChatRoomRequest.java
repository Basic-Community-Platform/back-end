package opensocial.org.community_hub.domain.chat.dto;

public class ChatRoomRequest {
    private String name;

    public ChatRoomRequest() {
    }

    public ChatRoomRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}