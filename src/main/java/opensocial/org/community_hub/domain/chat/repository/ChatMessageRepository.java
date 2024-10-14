package opensocial.org.community_hub.domain.chat.repository;

import opensocial.org.community_hub.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomId(String chatRoomId);
}