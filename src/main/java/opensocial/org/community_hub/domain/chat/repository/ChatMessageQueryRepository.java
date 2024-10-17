package opensocial.org.community_hub.domain.chat.repository;

import opensocial.org.community_hub.domain.chat.dto.ChatMessageResponse;
import opensocial.org.community_hub.domain.chat.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatMessageQueryRepository {
    List<ChatMessageResponse> findMessagesByRoomId(Long chatRoomId);

    Optional<ChatRoom> findByIdWithUsers(Long roomId);
}
