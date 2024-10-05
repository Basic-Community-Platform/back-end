package opensocial.org.community_hub.domain.chat.dto;

public class ChatRoomDto {

    private String id;
    private String name;

    // 생성자
    public ChatRoomDto() {
    }

    public ChatRoomDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter 및 Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
