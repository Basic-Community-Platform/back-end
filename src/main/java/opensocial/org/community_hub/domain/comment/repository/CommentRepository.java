package opensocial.org.community_hub.domain.comment.repository;

import opensocial.org.community_hub.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 게시물의 댓글 목록 조회
    @Query("SELECT c FROM Comment c WHERE c.post.postId = :postId ORDER BY c.createdAt ASC")
    List<Comment> findCommentsByPostId(@Param("postId") Long postId);
}
