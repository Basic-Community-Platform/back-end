package opensocial.org.community_hub.domain.chat.dto;

public class ChatRoomResponse {

    private String name;

    public ChatRoomResponse() {
    }

    public ChatRoomResponse(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
