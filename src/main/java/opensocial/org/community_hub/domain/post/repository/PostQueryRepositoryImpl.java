package opensocial.org.community_hub.domain.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import opensocial.org.community_hub.domain.post.dto.PostDTO;
import opensocial.org.community_hub.domain.post.dto.QPostDTO;
import opensocial.org.community_hub.domain.post.entity.QPost;
import opensocial.org.community_hub.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PostDTO> findByUser_NameContainingIgnoreCaseAndIgnoreSpaces(String keyword) {
        QPost post = QPost.post;
        return queryFactory.select(new QPostDTO(
                        post.postId,
                        post.title,
                        post.content,
                        post.viewCount,
                        post.commentCount,
                        post.user.name
                ))
                .from(post)
                .where(containsIgnoreCaseAndIgnoreSpaces(post.user.name, keyword))
                .fetch();
    }

    @Override
    public List<PostDTO> findByTitleContainingIgnoreCaseAndIgnoreSpaces(String keyword) {
        QPost post = QPost.post;
        return queryFactory.select(new QPostDTO(
                        post.postId,
                        post.title,
                        post.content,
                        post.viewCount,
                        post.commentCount,
                        post.user.name
                ))
                .from(post)
                .where(containsIgnoreCaseAndIgnoreSpaces(post.title, keyword))
                .fetch();
    }

    @Override
    public List<PostDTO> findPostsByContentContainingIgnoreCaseAndIgnoreSpaces(String keyword) {
        QPost post = QPost.post;
        return queryFactory.select(new QPostDTO(
                        post.postId,
                        post.title,
                        post.content,
                        post.viewCount,
                        post.commentCount,
                        post.user.name
                ))
                .from(post)
                .where(containsIgnoreCaseAndIgnoreSpaces(post.content, keyword))
                .fetch();
    }

    @Override
    public PostDTO findPreviousPost(Long postId) {
        QPost post = QPost.post;
        return queryFactory.select(new QPostDTO(
                        post.postId,
                        post.title,
                        post.content,
                        post.viewCount,
                        post.commentCount,
                        post.user.name
                ))
                .from(post)
                .where(post.postId.lt(postId))
                .orderBy(post.postId.desc())
                .limit(1) // 하나의 결과만 반환하도록 limit 설정
                .fetchOne();
    }

    @Override
    public PostDTO findNextPost(Long postId) {
        QPost post = QPost.post;
        return queryFactory.select(new QPostDTO(
                        post.postId,
                        post.title,
                        post.content,
                        post.viewCount,
                        post.commentCount,
                        post.user.name
                ))
                .from(post)
                .where(post.postId.gt(postId))
                .orderBy(post.postId.asc())
                .limit(1) // 하나의 결과만 반환하도록 limit 설정
                .fetchOne();
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

    @Override
    public List<PostDTO> findAllPostsAsDTO() {
        QPost post = QPost.post;
        QUser user = QUser.user;

        return queryFactory
                .select(new QPostDTO(
                        post.postId,
                        post.title,
                        post.content,
                        post.viewCount,
                        post.commentCount,
                        user.name
                ))
                .from(post)
                .leftJoin(post.user, user)
                .fetch();
    }
}
