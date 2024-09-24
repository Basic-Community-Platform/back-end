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
    public PostDTO createPost(Post post, User user) {
        post.setUser(user); // 게시글에 사용자 정보 추가
        Post savedPost = postRepository.save(post);
        return convertToDTO(savedPost);
    }

    // 게시글 조회
    @Transactional(readOnly = true)
    public Optional<PostDTO> getPostById(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        return post.map(this::convertToDTO);
    }

    // 게시글 업데이트 (본인 게시글만 업데이트)
    public PostDTO updatePost(Long postId, Post postDetails, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + postId));

        if (!post.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("You are not authorized to update this post");
        }

        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());
        post.setViewCount(postDetails.getViewCount());
        return convertToDTO(postRepository.save(post));
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
                        .map(postDTO -> new PostDTO(postDTO.getPostId(), postDTO.getLoginId(), postDTO.getTitle(), postDTO.getContent(), postDTO.getViewCount(), postDTO.getCommentCount(), postDTO.getUserName()))
                        .toList();  // Post 엔티티를 PostDTO로 변환하여 반환
            case TITLE:
                return postRepository.findByTitleContainingIgnoreCaseAndIgnoreSpaces(keyword)
                        .stream()
                        .map(postDTO -> new PostDTO(postDTO.getPostId(), postDTO.getLoginId(), postDTO.getTitle(), postDTO.getContent(), postDTO.getViewCount(), postDTO.getCommentCount(),  postDTO.getUserName()))
                        .toList();
            case CONTENT:
                return postRepository.findPostsByContentContainingIgnoreCaseAndIgnoreSpaces(keyword)
                        .stream()
                        .map(postDTO -> new PostDTO(postDTO.getPostId(), postDTO.getLoginId(), postDTO.getTitle(), postDTO.getContent(), postDTO.getViewCount(), postDTO.getCommentCount(), postDTO.getUserName()))
                        .toList();
            default:
                throw new IllegalArgumentException("Invalid search type");
        }
    }

    // 이전 게시물 찾기
    public Optional<PostDTO> findPreviousPost(Long postId) {
        PostDTO post = postRepository.findPreviousPost(postId);
        return Optional.ofNullable(post);
    }

    // 다음 게시물 찾기
    public Optional<PostDTO> findNextPost(Long postId) {
        PostDTO post = postRepository.findNextPost(postId);
        return Optional.ofNullable(post);
    }

    private PostDTO convertToDTO(Post post) {
        return new PostDTO(
                post.getPostId(),
                post.getUser().getLoginId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getCommentCount(),
                post.getUser().getName()
        );
    }

    //QueryDSL 사용한 DTO 리스트 리턴
    public List<PostDTO> getAllPosts() {
        return postRepository.findAllPostsAsDTO();
    }
}
