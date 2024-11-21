package opensocial.org.community_hub.domain.chat.repository;

import opensocial.org.community_hub.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}