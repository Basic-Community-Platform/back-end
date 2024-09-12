package opensocial.org.community_hub.domain.post.repository;

import opensocial.org.community_hub.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser_NameContaining(String keyword);
    List<Post> findByTitleContaining(String keyword);
    List<Post> findPostsByContentContaining(String keyword);
}
