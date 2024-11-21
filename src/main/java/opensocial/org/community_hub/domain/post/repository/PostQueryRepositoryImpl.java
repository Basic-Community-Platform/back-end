package opensocial.org.community_hub.domain.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import opensocial.org.community_hub.domain.post.dto.PostResponse;
import opensocial.org.community_hub.domain.post.dto.UserInfoDTO;
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

    // Username으로 검색
    @Override
    public List<PostResponse> findByUser_NameContainingIgnoreCaseAndIgnoreSpaces(String keyword) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        List<Post> posts = queryFactory.selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .where(containsIgnoreCaseAndIgnoreSpaces(post.user.name, keyword))
                .fetch();

        return posts.stream()
                .map(this::convertToPostResponse)
                .collect(Collectors.toList());
    }

    // Title로 검색
    @Override
    public List<PostResponse> findByTitleContainingIgnoreCaseAndIgnoreSpaces(String keyword) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        List<Post> posts = queryFactory.selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .where(containsIgnoreCaseAndIgnoreSpaces(post.title, keyword))
                .fetch();

        return posts.stream()
                .map(this::convertToPostResponse)
                .collect(Collectors.toList());
    }

    // Content로 검색
    @Override
    public List<PostResponse> findPostsByContentContainingIgnoreCaseAndIgnoreSpaces(String keyword) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        List<Post> posts = queryFactory.selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .where(containsIgnoreCaseAndIgnoreSpaces(post.content, keyword))
                .fetch();

        return posts.stream()
                .map(this::convertToPostResponse)
                .collect(Collectors.toList());
    }

    // 이전 게시물 찾기
    @Override
    public PostResponse findPreviousPost(Long postId) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        Post result = queryFactory.selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .where(post.postId.lt(postId))
                .orderBy(post.postId.desc())
                .limit(1)
                .fetchOne();

        return result != null ? convertToPostResponse(result) : null;
    }

    // 다음 게시물 찾기
    @Override
    public PostResponse findNextPost(Long postId) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        Post result = queryFactory.selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .where(post.postId.gt(postId))
                .orderBy(post.postId.asc())
                .limit(1)
                .fetchOne();

        return result != null ? convertToPostResponse(result) : null;
    }

    // 모든 게시물 조회
    @Override
    public List<PostResponse> findAllPostsAsDTO() {
        QPost post = QPost.post;
        QUser user = QUser.user;

        List<Post> posts = queryFactory.selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .fetch();

        return posts.stream()
                .map(this::convertToPostResponse)
                .collect(Collectors.toList());
    }

    // Post를 PostResponse로 변환
    private PostResponse convertToPostResponse(Post post) {
        UserInfoDTO userInfo = new UserInfoDTO(
                post.getUser().getLoginId(),
                post.getUser().getName(),
                post.getUser().getProfileImageUrl(),
                post.getUser().getEmail()
        );

        return new PostResponse(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getCommentCount(),
                userInfo
        );
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
