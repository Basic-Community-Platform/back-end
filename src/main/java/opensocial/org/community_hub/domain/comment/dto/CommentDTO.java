package opensocial.org.community_hub.domain.comment.dto;

import lombok.Data;
import opensocial.org.community_hub.domain.user.entity.User;

import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long commentId;
    private String content;
    private String username;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentDTO(Long commentId, String content, String username, Long postId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.commentId = commentId;
        this.content = content;
        this.username = username;
        this.postId = postId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
