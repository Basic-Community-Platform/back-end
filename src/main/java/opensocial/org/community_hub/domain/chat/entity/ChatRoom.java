package opensocial.org.community_hub.domain.chat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import opensocial.org.community_hub.domain.user.entity.User;

import java.util.HashSet;
import java.util.Set;

@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long roomId;

    @Setter
    @Getter
    @Column(nullable = false)
    private String roomName;

    // 채팅방에 속한 유저 목록
    @ManyToMany
    @JoinTable(
            name = "chat_room_users", // 연결 테이블 이름
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Getter
    private Set<User> users = new HashSet<>();

    public ChatRoom() {
    }

    public ChatRoom(String roomName) {
        this.roomName = roomName;
    }

    // 유저 추가 메서드
    public void addUser(User user) {
        this.users.add(user);
    }

    // 유저 제거 메서드
    public void removeUser(User user) {
        this.users.remove(user);
    }
}
