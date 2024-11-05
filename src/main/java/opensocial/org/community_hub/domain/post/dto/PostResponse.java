package opensocial.org.community_hub.domain.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class PostResponse {
    private Long postId;
    private String title;
    private String content;
    private int viewCount;
    private int commentCount;
    private UserInfoDTO userInfo;

    //QueryDSL을 사용해 QPostDTO 클래스 생성할 수 있도록 추가
    @QueryProjection
    public PostResponse(Long postId, String title, String content, int viewCount, int commentCount, UserInfoDTO userInfo) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.userInfo = userInfo;
    }
}
