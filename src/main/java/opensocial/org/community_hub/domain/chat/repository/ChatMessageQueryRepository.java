package opensocial.org.community_hub.domain.chat.repository;

import opensocial.org.community_hub.domain.chat.dto.ChatMessageResponse;

import java.util.List;

public interface ChatMessageQueryRepository {
    List<ChatMessageResponse> findMessagesByRoomId(Long chatRoomId);
}
