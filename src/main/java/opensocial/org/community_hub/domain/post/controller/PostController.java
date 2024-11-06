package opensocial.org.community_hub.domain.post.controller;

import lombok.RequiredArgsConstructor;
import opensocial.org.community_hub.domain.post.dto.PostResponse;
import opensocial.org.community_hub.domain.post.dto.SearchRequest;
import opensocial.org.community_hub.domain.post.entity.Post;
import opensocial.org.community_hub.domain.post.enums.PostSearchType;
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

    // 전체 게시글 조회 및 검색
    @GetMapping("")
    public ResponseEntity<?> getPosts(
            @RequestParam(defaultValue = "0") int page,            // 현재 페이지 (기본값 0)
            @RequestParam(defaultValue = "10") int size,           // 페이지 크기 (기본값 10)
            @RequestParam(required = false) String keyword,        // 검색 키워드
            @RequestParam(required = false) PostSearchType searchType // 검색 타입
    ) {
        System.out.println(searchType);
        if (keyword != null && searchType != null) {
            // 특정 조건으로 게시글 검색
            SearchRequest searchRequest = new SearchRequest(keyword, searchType);
            List<PostResponse> posts = postService.searchPosts(searchRequest);
            return ResponseEntity.ok(posts);
        } else {
            // 모든 게시글 조회
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<PostResponse> posts = postService.getAllPosts(pageable);
            return ResponseEntity.ok(posts);
        }
    }

    // 단일 게시글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
        return postService.getPostById(postId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
