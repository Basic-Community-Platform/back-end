package opensocial.org.community_hub.service;

import lombok.RequiredArgsConstructor;
import opensocial.org.community_hub.entity.Post;
import opensocial.org.community_hub.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // 게시글 생성
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    // 게시글 조회
    public Optional<Post> getPostById(Long postId) {
        return postRepository.findById(postId);
    }

    // 게시글 업데이트
    public Post updatePost(Long postId, Post postDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + postId));

        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());
        post.setViewCount(postDetails.getViewCount());
        post.setCommentCount(postDetails.getCommentCount());

        return postRepository.save(post);
    }

    // 게시글 삭제
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + postId));
        postRepository.delete(post);
    }
}
