package opensocial.org.community_hub.domain.chat.dto;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public class ChatMessageResponse {
    private String messageContent;
    private String senderUsername;
    private LocalDateTime sentAt;

    @QueryProjection
    public ChatMessageResponse(String messageContent, String senderUsername, LocalDateTime sentAt) {
        this.messageContent = messageContent;
        this.senderUsername = senderUsername;
        this.sentAt = sentAt;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
