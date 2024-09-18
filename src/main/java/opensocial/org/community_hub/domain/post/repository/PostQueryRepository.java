package opensocial.org.community_hub.domain.post.repository;
import opensocial.org.community_hub.domain.post.dto.PostDTO;

import java.util.List;

public interface PostQueryRepository {
    List<PostDTO> findAllPostsAsDTO();
}