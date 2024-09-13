package opensocial.org.community_hub.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensocial.org.community_hub.domain.post.dto.PostDTO;
import opensocial.org.community_hub.domain.post.dto.SearchRequest;
import opensocial.org.community_hub.domain.post.entity.Post;
import opensocial.org.community_hub.domain.post.enums.PostSearchType;
import opensocial.org.community_hub.domain.post.repository.PostRepository;
import opensocial.org.community_hub.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // 게시글 생성
    public Post createPost(Post post, User user) {

        post.setUser(user); // 게시글에 사용자 정보 추가
        return postRepository.save(post);
    }

    // 게시글 조회
    @Transactional(readOnly = true)
    public Optional<Post> getPostById(Long postId) {
        return postRepository.findById(postId);
    }

    // 게시글 업데이트 (본인 게시글만 업데이트)
    public Post updatePost(Long postId, Post postDetails, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + postId));

        if (!post.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("You are not authorized to update this post");
        }

        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());
        post.setViewCount(postDetails.getViewCount());
        return postRepository.save(post);
    }

    // 게시글 삭제 (본인 게시글만 삭제)
    public void deletePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + postId));

        if (!post.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("You are not authorized to delete this post");
        }

        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public List<PostDTO> searchPosts(SearchRequest searchRequest) {
        String keyword = searchRequest.getKeyword();
        PostSearchType searchType = searchRequest.getSearchType();

        switch (searchType) {
            case USERNAME:
                return postRepository.findByUser_NameContainingIgnoreCaseAndIgnoreSpaces(keyword)
                        .stream()
                        .map(post -> new PostDTO(post.getPostId(), post.getTitle(), post.getContent(), post.getViewCount(), post.getCommentCount(), post.getUser().getName()))
                        .toList();  // Post 엔티티를 PostDTO로 변환하여 반환
            case TITLE:
                return postRepository.findByTitleContainingIgnoreCaseAndIgnoreSpaces(keyword)
                        .stream()
                        .map(post -> new PostDTO(post.getPostId(), post.getTitle(), post.getContent(), post.getViewCount(), post.getCommentCount(),  post.getUser().getName()))
                        .toList();
            case CONTENT:
                return postRepository.findPostsByContentContainingIgnoreCaseAndIgnoreSpaces(keyword)
                        .stream()
                        .map(post -> new PostDTO(post.getPostId(), post.getTitle(), post.getContent(), post.getViewCount(), post.getCommentCount(), post.getUser().getName()))
                        .toList();
            default:
                throw new IllegalArgumentException("Invalid search type");
        }
    }
}
