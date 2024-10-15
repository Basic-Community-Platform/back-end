package opensocial.org.community_hub.domain.chat.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class ChatRoom {

    @Id
    @Column(name = "room_id")
    private String id;

    @Column(nullable = false)
    private String roomName;

    public static ChatRoom create(String roomName) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.id = UUID.randomUUID().toString();
        chatRoom.roomName = roomName;
        return chatRoom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return roomName;
    }

    public void setName(String roomName) {
        this.roomName = roomName;
    }
}
