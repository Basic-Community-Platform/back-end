package opensocial.org.community_hub.domain.chat.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatRoomResponse {

    private Long roomId;
    private String name;
    private String profileImageUrl;
    private LocalDateTime createdAt; // 최초 메시지 보낸 시간
    private LocalDateTime updatedAt; // 마지막으로 보낸 시간
}
