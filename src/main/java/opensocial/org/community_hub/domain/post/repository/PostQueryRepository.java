package opensocial.org.community_hub.domain.post.repository;
import opensocial.org.community_hub.domain.post.dto.PostDTO;

import java.util.List;

public interface PostQueryRepository {
    List<PostDTO> findAllPostsAsDTO();
    List<PostDTO> findByUser_NameContainingIgnoreCaseAndIgnoreSpaces(String keyword);
    List<PostDTO> findByTitleContainingIgnoreCaseAndIgnoreSpaces(String keyword);
    List<PostDTO> findPostsByContentContainingIgnoreCaseAndIgnoreSpaces(String keyword);
    PostDTO findPreviousPost(Long postId);
    PostDTO findNextPost(Long postId);
}