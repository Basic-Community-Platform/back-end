package opensocial.org.community_hub.domain.post.dto;

import lombok.Data;

@Data
public class PostDTO {
    private Long postId;
    private String title;
    private String content;
    private int viewCount;
    private int commentCount;
    private String userName;

    public PostDTO(Long postId, String title, String content, int viewCount, int commentCount, String userName) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.userName = userName;
    }
}
