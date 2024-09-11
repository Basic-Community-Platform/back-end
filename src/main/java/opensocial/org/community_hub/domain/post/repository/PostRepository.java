package opensocial.org.community_hub.domain.post.repository;

import opensocial.org.community_hub.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
