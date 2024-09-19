package opensocial.org.community_hub.domain.post.controller;

import lombok.RequiredArgsConstructor;
import opensocial.org.community_hub.domain.post.dto.PostDTO;
import opensocial.org.community_hub.domain.post.dto.SearchRequest;
import opensocial.org.community_hub.domain.post.entity.Post;
import opensocial.org.community_hub.domain.post.service.PostService;
import opensocial.org.community_hub.domain.user.entity.User;
import opensocial.org.community_hub.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    // 게시글 생성 (로그인한 사용자 정보를 받아서 생성)
    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody Post post, @AuthenticationPrincipal UserDetails userDetails) {
        PostDTO createdPost = postService.createPost(post, userDetails); //Post 정보에 User 정보 연결

        return ResponseEntity.ok(createdPost);
    }

    // 전체 게시글 조회
    @GetMapping("")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // 단일 게시글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long postId) {
        return postService.getPostById(postId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 게시글 업데이트 (로그인한 사용자만 수정 가능)
    @PutMapping("/{postId}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long postId, @RequestBody Post postDetails, @AuthenticationPrincipal UserDetails userDetails) {
        PostDTO updatedPost = postService.updatePost(postId, postDetails, userDetails);
        return ResponseEntity.ok(updatedPost);
    }

    // 게시글 삭제 (로그인한 사용자만 삭제 가능)
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(postId, userDetails);
        return ResponseEntity.noContent().build();
    }

    // username, title, content 중 선택하여 게시글 검색
    @PostMapping("/search")
    public ResponseEntity<List<PostDTO>> searchPosts(@RequestBody SearchRequest searchRequest) {
        List<PostDTO> posts = postService.searchPosts(searchRequest);
        return ResponseEntity.ok(posts);
    }

    // 이전 게시물 조회
    @GetMapping("/{postId}/previous")
    public ResponseEntity<PostDTO> getPreviousPost(@PathVariable Long postId) {
        return postService.findPreviousPost(postId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 다음 게시물 조회
    @GetMapping("/{postId}/next")
    public ResponseEntity<PostDTO> getNextPost(@PathVariable Long postId) {
        return postService.findNextPost(postId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
