package opensocial.org.community_hub.domain.post.repository;

import opensocial.org.community_hub.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE LOWER(REPLACE(p.user.name, ' ', '')) LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%'))")
    List<Post> findByUser_NameContainingIgnoreCaseAndIgnoreSpaces(@Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE LOWER(REPLACE(p.title, ' ', '')) LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%'))")
    List<Post> findByTitleContainingIgnoreCaseAndIgnoreSpaces(@Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE LOWER(REPLACE(p.content, ' ', '')) LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%'))")
    List<Post> findPostsByContentContainingIgnoreCaseAndIgnoreSpaces(@Param("keyword") String keyword);
}
