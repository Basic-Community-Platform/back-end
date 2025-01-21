package opensocial.org.community_hub.domain.chat.repository;

import opensocial.org.community_hub.domain.chat.entity.ChatMessage;
import opensocial.org.community_hub.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomRoomId(Long chatRoomId);

    // 가장 오래된 메시지
    Optional<ChatMessage> findFirstByChatRoomOrderByTimestampAsc(ChatRoom chatRoom);

    // 가장 최근 메시지
    Optional<ChatMessage> findFirstByChatRoomOrderByTimestampDesc(ChatRoom chatRoom);
}
