package opensocial.org.community_hub.domain.chat.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class ChatRoom {

    @Id
    @Column(name = "room_id")
    private String id;

    @Column(nullable = false)
    private String name;

    public static ChatRoom create(String name) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.id = UUID.randomUUID().toString();
        chatRoom.name = name;
        return chatRoom;
    }

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
