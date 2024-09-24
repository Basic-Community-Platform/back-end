package opensocial.org.community_hub.domain.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class PostDTO {
    private Long postId;
    private String loginId;
    private String title;
    private String content;
    private int viewCount;
    private int commentCount;
    private String userName;

    //QueryDSL을 사용해 QPostDTO 클래스 생성할 수 있도록 추가
    @QueryProjection
    public PostDTO(Long postId, String loginId, String title, String content, int viewCount, int commentCount, String userName) {
        this.postId = postId;
        this.loginId = loginId;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.userName = userName;
    }
}
