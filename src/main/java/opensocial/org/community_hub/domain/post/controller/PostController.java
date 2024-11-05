package opensocial.org.community_hub.domain.post.controller;

import lombok.RequiredArgsConstructor;
import opensocial.org.community_hub.domain.post.dto.PostResponse;
import opensocial.org.community_hub.domain.post.dto.SearchRequest;
import opensocial.org.community_hub.domain.post.entity.Post;
import opensocial.org.community_hub.domain.post.service.PostService;
import opensocial.org.community_hub.domain.user.entity.User;
import opensocial.org.community_hub.domain.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<PostResponse> createPost(@RequestBody Post post, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUserDetails(userDetails);
        PostResponse createdPost = postService.createPost(post, user); //Post 정보에 User 정보 연결

        return ResponseEntity.ok(createdPost);
    }

    // 페이지네이션을 적용한 전체 게시글 조회
    @GetMapping("")
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,            // 현재 페이지 (기본값 0)
            @RequestParam(defaultValue = "10") int size            // 페이지 크기 (기본값 10)
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending()); // 최신순 정렬
        Page<PostResponse> posts = postService.getAllPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    // 단일 게시글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
        return postService.getPostById(postId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 게시글 업데이트 (로그인한 사용자만 수정 가능)
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long postId, @RequestBody Post postDetails, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUserDetails(userDetails);
        PostResponse updatedPost = postService.updatePost(postId, postDetails, user);
        return ResponseEntity.ok(updatedPost);
    }

    // 게시글 삭제 (로그인한 사용자만 삭제 가능)
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUserDetails(userDetails);
        postService.deletePost(postId, user);
        return ResponseEntity.noContent().build();
    }

    // username, title, content 중 선택하여 게시글 검색
    @PostMapping("/search")
    public ResponseEntity<List<PostResponse>> searchPosts(@RequestBody SearchRequest searchRequest) {
        List<PostResponse> posts = postService.searchPosts(searchRequest);
        return ResponseEntity.ok(posts);
    }

    // 이전 게시물 조회
    @GetMapping("/{postId}/previous")
    public ResponseEntity<PostResponse> getPreviousPost(@PathVariable Long postId) {
        return postService.findPreviousPost(postId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 다음 게시물 조회
    @GetMapping("/{postId}/next")
    public ResponseEntity<PostResponse> getNextPost(@PathVariable Long postId) {
        return postService.findNextPost(postId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
