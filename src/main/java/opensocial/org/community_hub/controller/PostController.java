package opensocial.org.community_hub.controller;

import lombok.RequiredArgsConstructor;
import opensocial.org.community_hub.entity.Post;
import opensocial.org.community_hub.entity.User;
import opensocial.org.community_hub.service.PostService;
import opensocial.org.community_hub.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    // 게시글 생성 (로그인한 사용자 정보를 받아서 생성)
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post, @AuthenticationPrincipal UserDetails userDetails) {
        // UserDetails에서 로그인 ID 가져오기
        String loginId = userDetails.getUsername();

        // 로그인 ID로 실제 User 엔티티를 조회
        User user = userService.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 게시글 생성 시 User 엔티티를 연결
        Post createdPost = postService.createPost(post, user);

        return ResponseEntity.ok(createdPost);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable Long postId) {
        Optional<Post> post = postService.getPostById(postId);
        return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 게시글 업데이트 (로그인한 사용자만 수정 가능)
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody Post postDetails, @AuthenticationPrincipal User user) {
        Post updatedPost = postService.updatePost(postId, postDetails, user);
        return ResponseEntity.ok(updatedPost);
    }

    // 게시글 삭제 (로그인한 사용자만 삭제 가능)
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        postService.deletePost(postId, user);
        return ResponseEntity.noContent().build();
    }
}
