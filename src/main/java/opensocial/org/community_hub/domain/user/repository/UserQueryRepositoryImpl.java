package opensocial.org.community_hub.domain.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import opensocial.org.community_hub.domain.user.dto.UserDetailsResponse;
import opensocial.org.community_hub.domain.user.entity.QUser;
import opensocial.org.community_hub.domain.post.entity.QPost;
import opensocial.org.community_hub.domain.comment.entity.QComment;
import opensocial.org.community_hub.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public UserDetailsResponse findUserDetailsByLoginId(String loginId) {
        QUser qUser = QUser.user;
        QPost qPost = QPost.post;
        QComment qComment = QComment.comment;

        User user = queryFactory.selectFrom(qUser)
                .where(qUser.loginId.eq(loginId))
                .fetchOne();

        if (user == null) {
            return null;
        }

        List<String> postTitles = queryFactory.select(qPost.title)
                .from(qPost)
                .where(qPost.user.loginId.eq(loginId))
                .fetch();

        List<String> commentContents = queryFactory.select(qComment.content)
                .from(qComment)
                .where(qComment.user.loginId.eq(loginId))
                .fetch();

        return new UserDetailsResponse(
                user.getProfileImageUrl(),
                user.getName(),
                user.getLoginId(),
                user.getEmail(),
                postTitles,
                commentContents
        );
    }

    @Override
    public UserDetailsResponse findUserBasicInfoByLoginId(String loginId) {
        QUser qUser = QUser.user;

        User user = queryFactory.selectFrom(qUser)
                .where(qUser.loginId.eq(loginId))
                .fetchOne();

        if (user == null) {
            return null;
        }

        return new UserDetailsResponse(
                user.getProfileImageUrl(),
                user.getName(),
                user.getLoginId(),
                user.getEmail(),
                null,  // postTitles를 null로 설정
                null   // commentContents를 null로 설정
        );
    }
}
