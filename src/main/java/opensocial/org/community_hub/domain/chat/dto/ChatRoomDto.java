package opensocial.org.community_hub.domain.chat.dto;

public class ChatRoomDto {

    private String name;

    public ChatRoomDto() {
    }

    public ChatRoomDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
