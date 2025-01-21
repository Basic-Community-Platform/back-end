package opensocial.org.community_hub.domain.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import opensocial.org.community_hub.domain.chat.dto.ChatMessageResponse;
import opensocial.org.community_hub.domain.chat.dto.QChatMessageResponse;
import opensocial.org.community_hub.domain.chat.entity.ChatRoom;
import opensocial.org.community_hub.domain.chat.entity.QChatMessage;
import opensocial.org.community_hub.domain.chat.entity.QChatRoom;
import opensocial.org.community_hub.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class ChatMessageQueryRepositoryImpl implements ChatMessageQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ChatMessageQueryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ChatMessageResponse> findMessagesByRoomId(Long chatRoomId) {
        QChatMessage chatMessage = QChatMessage.chatMessage;
        QUser user = QUser.user;

        // QueryDSL로 필요한 데이터만 조회
        return queryFactory.select(
                        new QChatMessageResponse(
                                chatMessage.content,
                                user.name,
                                user.loginId,
                                chatMessage.timestamp,
                                user.profileImageUrl
                        ))
                .from(chatMessage)
                .join(chatMessage.user, user) // User와 JOIN
                .where(chatMessage.chatRoom.roomId.eq(chatRoomId))
                .fetch();
    }

    @Override
    public Optional<ChatRoom> findByIdWithUsers(Long roomId) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QUser user = QUser.user;

        ChatRoom result = queryFactory.selectFrom(chatRoom)
                .leftJoin(chatRoom.users, user).fetchJoin() // JOIN FETCH로 users 로드
                .where(chatRoom.roomId.eq(roomId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
