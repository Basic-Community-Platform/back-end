package opensocial.org.community_hub.domain.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import opensocial.org.community_hub.domain.post.dto.PostDTO;
import lombok.RequiredArgsConstructor;
import opensocial.org.community_hub.domain.post.dto.QPostDTO;
import opensocial.org.community_hub.domain.post.entity.QPost;
import opensocial.org.community_hub.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

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
