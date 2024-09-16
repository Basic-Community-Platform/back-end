package opensocial.org.community_hub.domain.comment.controller;

import lombok.RequiredArgsConstructor;
import opensocial.org.community_hub.domain.comment.dto.CommentDTO;
import opensocial.org.community_hub.domain.comment.entity.Comment;
import opensocial.org.community_hub.domain.comment.service.CommentService;
import opensocial.org.community_hub.domain.user.entity.User;
import opensocial.org.community_hub.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    // 댓글 생성 (Create)
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@PathVariable Long postId, @RequestBody Comment comment, @AuthenticationPrincipal UserDetails userDetails) {
        // UserDetails에서 로그인 ID 가져오기
        String loginId = userDetails.getUsername();

        // 로그인 ID로 실제 User 엔티티를 조회
        User user = userService.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CommentDTO createdComment = commentService.createComment(postId, comment, user);
        return ResponseEntity.ok(createdComment);
    }

    // 특정 게시물의 모든 댓글 조회 (Read)
    @GetMapping
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable Long postId) {
        List<CommentDTO> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 수정 (Update)
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody Comment comment, @AuthenticationPrincipal UserDetails userDetails){
        // UserDetails에서 로그인 ID 가져오기
        String loginId = userDetails.getUsername();

        // 로그인 ID로 실제 User 엔티티를 조회
        User user = userService.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CommentDTO updatedComment = commentService.updateComment(postId, commentId, comment, user);
        return ResponseEntity.ok(updatedComment);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, @AuthenticationPrincipal UserDetails userDetails){
        // UserDetails에서 로그인 ID 가져오기
        String loginId = userDetails.getUsername();

        // 로그인 ID로 실제 User 엔티티를 조회
        User user = userService.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        commentService.deleteComment(postId, commentId, user);
        return ResponseEntity.noContent().build();
    }
}
