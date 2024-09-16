package opensocial.org.community_hub.domain.post.repository;

import opensocial.org.community_hub.domain.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // name, title, content 기반 게시물 검색 기능
    @Query("SELECT p FROM Post p WHERE LOWER(REPLACE(p.user.name, ' ', '')) LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%'))")
    List<Post> findByUser_NameContainingIgnoreCaseAndIgnoreSpaces(@Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE LOWER(REPLACE(p.title, ' ', '')) LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%'))")
    List<Post> findByTitleContainingIgnoreCaseAndIgnoreSpaces(@Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE LOWER(REPLACE(p.content, ' ', '')) LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%'))")
    List<Post> findPostsByContentContainingIgnoreCaseAndIgnoreSpaces(@Param("keyword") String keyword);

    // 이전 게시물 중 첫 번째 결과만 반환
    @Query("SELECT p FROM Post p WHERE p.postId < :postId ORDER BY p.postId DESC")
    List<Post> findPreviousPost(@Param("postId") Long postId, Pageable pageable);

    // 다음 게시물 중 첫 번째 결과만 반환
    @Query("SELECT p FROM Post p WHERE p.postId > :postId ORDER BY p.postId ASC")
    List<Post> findNextPost(@Param("postId") Long postId, Pageable pageable);
}
