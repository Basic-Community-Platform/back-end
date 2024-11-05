package opensocial.org.community_hub.domain.post.repository;
import opensocial.org.community_hub.domain.post.dto.PostResponse;

import java.util.List;

public interface PostQueryRepository {
    List<PostResponse> findAllPostsAsDTO();
    List<PostResponse> findByUser_NameContainingIgnoreCaseAndIgnoreSpaces(String keyword);
    List<PostResponse> findByTitleContainingIgnoreCaseAndIgnoreSpaces(String keyword);
    List<PostResponse> findPostsByContentContainingIgnoreCaseAndIgnoreSpaces(String keyword);
    PostResponse findPreviousPost(Long postId);
    PostResponse findNextPost(Long postId);
}