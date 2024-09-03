package opensocial.org.community_hub.repository;

import opensocial.org.community_hub.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
