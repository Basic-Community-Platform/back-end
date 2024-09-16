package opensocial.org.community_hub.domain.comment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import opensocial.org.community_hub.domain.comment.dto.CommentDTO;
import opensocial.org.community_hub.domain.comment.entity.Comment;
import opensocial.org.community_hub.domain.comment.repository.CommentRepository;
import opensocial.org.community_hub.domain.post.entity.Post;
import opensocial.org.community_hub.domain.post.repository.PostRepository;
import opensocial.org.community_hub.domain.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 댓글 생성 (Create)
    public CommentDTO createComment(Long postId, CommentDTO commentDTO, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));

        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setPost(post); // 게시물과 댓글을 연결
        comment.setUser(user); // 게시물과 유저를 연결

        Comment savedComment = commentRepository.save(comment);
        return convertToDTO(savedComment);
    }

    // 특정 게시물의 모든 댓글 조회 (Read)
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));

        List<Comment> comments = commentRepository.findCommentsByPostId(post.getPostId());
        return comments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 엔티티를 DTO로 변환
    private CommentDTO convertToDTO(Comment comment) {
        return new CommentDTO(
                comment.getCommentId(),
                comment.getContent(),
                comment.getUser().getName(),
                comment.getPost().getPostId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt());
    }
}
