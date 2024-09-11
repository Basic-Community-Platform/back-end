package opensocial.org.community_hub.domain.post.dto;

import lombok.Data;
import opensocial.org.community_hub.domain.post.enums.PostSearchType;

@Data
public class SearchRequest {
    private PostSearchType searchType;  // 검색할 필드 (유저명, 제목, 내용)
    private String keyword;         // 검색 키워드
}
