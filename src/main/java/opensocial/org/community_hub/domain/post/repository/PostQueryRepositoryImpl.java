package opensocial.org.community_hub.domain.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import opensocial.org.community_hub.domain.post.dto.PostDTO;
import opensocial.org.community_hub.domain.post.entity.Post;
import opensocial.org.community_hub.domain.post.entity.QPost;
import opensocial.org.community_hub.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PostDTO> findByUser_NameContainingIgnoreCaseAndIgnoreSpaces(String keyword) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        // QueryDsl과 Fetch Join을 사용해 엔티티 조회
        List<Post> posts = queryFactory.selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .where(containsIgnoreCaseAndIgnoreSpaces(post.user.name, keyword))
                .fetch();

        // DTO로 변환
        return posts.stream()
                .map(p -> new PostDTO(
                        p.getPostId(),
                        p.getUser().getLoginId(),
                        p.getTitle(),
                        p.getContent(),
                        p.getViewCount(),
                        p.getCommentCount(),
                        p.getUser().getName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> findByTitleContainingIgnoreCaseAndIgnoreSpaces(String keyword) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        List<Post> posts = queryFactory.selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .where(containsIgnoreCaseAndIgnoreSpaces(post.title, keyword))
                .fetch();

        return posts.stream()
                .map(p -> new PostDTO(
                        p.getPostId(),
                        p.getUser().getLoginId(),
                        p.getTitle(),
                        p.getContent(),
                        p.getViewCount(),
                        p.getCommentCount(),
                        p.getUser().getName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> findPostsByContentContainingIgnoreCaseAndIgnoreSpaces(String keyword) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        List<Post> posts = queryFactory.selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .where(containsIgnoreCaseAndIgnoreSpaces(post.content, keyword))
                .fetch();

        return posts.stream()
                .map(p -> new PostDTO(
                        p.getPostId(),
                        p.getUser().getLoginId(),
                        p.getTitle(),
                        p.getContent(),
                        p.getViewCount(),
                        p.getCommentCount(),
                        p.getUser().getName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public PostDTO findPreviousPost(Long postId) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        Post result = queryFactory.selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .where(post.postId.lt(postId))
                .orderBy(post.postId.desc())
                .limit(1)
                .fetchOne();

        return result != null ? new PostDTO(
                result.getPostId(),
                result.getUser().getLoginId(),
                result.getTitle(),
                result.getContent(),
                result.getViewCount(),
                result.getCommentCount(),
                result.getUser().getName()
        ) : null;
    }

    @Override
    public PostDTO findNextPost(Long postId) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        Post result = queryFactory.selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .where(post.postId.gt(postId))
                .orderBy(post.postId.asc())
                .limit(1)
                .fetchOne();

        return result != null ? new PostDTO(
                result.getPostId(),
                result.getUser().getLoginId(),
                result.getTitle(),
                result.getContent(),
                result.getViewCount(),
                result.getCommentCount(),
                result.getUser().getName()
        ) : null;
    }

    @Override
    public List<PostDTO> findAllPostsAsDTO() {
        QPost post = QPost.post;
        QUser user = QUser.user;

        List<Post> posts = queryFactory.selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .fetch();

        return posts.stream()
                .map(p -> new PostDTO(
                        p.getPostId(),
                        p.getUser().getLoginId(),
                        p.getTitle(),
                        p.getContent(),
                        p.getViewCount(),
                        p.getCommentCount(),
                        p.getUser().getName()
                ))
                .collect(Collectors.toList());
    }

    private BooleanExpression containsIgnoreCaseAndIgnoreSpaces(StringPath path, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        String formattedKeyword = keyword.replace(" ", "").toLowerCase();
        return Expressions.stringTemplate(
                "LOWER(REPLACE({0}, ' ', ''))", path
        ).contains(formattedKeyword);
    }
}
