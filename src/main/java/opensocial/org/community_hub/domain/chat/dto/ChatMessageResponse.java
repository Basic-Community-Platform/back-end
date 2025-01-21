package opensocial.org.community_hub.domain.chat.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatMessageResponse {
    private String messageContent;
    private String senderUsername; // 사용자 이름
    private String senderLoginId;  // 사용자 로그인 ID
    private LocalDateTime sentAt;
    private Boolean isMyMessage;
    private String profileImageUrl;

    @QueryProjection
    public ChatMessageResponse(String messageContent, String senderUsername, String senderLoginId, LocalDateTime sentAt, String profileImageUrl) {
        this.messageContent = messageContent;
        this.senderUsername = senderUsername;
        this.senderLoginId = senderLoginId;
        this.sentAt = sentAt;
        this.profileImageUrl = profileImageUrl;
    }
}