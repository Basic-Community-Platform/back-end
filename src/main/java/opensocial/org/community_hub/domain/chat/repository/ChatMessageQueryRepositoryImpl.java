package opensocial.org.community_hub.domain.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import opensocial.org.community_hub.domain.chat.dto.ChatMessageResponse;
import opensocial.org.community_hub.domain.chat.dto.QChatMessageResponse;
import opensocial.org.community_hub.domain.chat.entity.QChatMessage;
import opensocial.org.community_hub.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.util.List;

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

        return queryFactory
                .select(new QChatMessageResponse(
                        chatMessage.content,
                        user.name,
                        chatMessage.timestamp))
                .from(chatMessage)
                .join(chatMessage.user, user).fetchJoin()  // fetch join 사용
                .where(chatMessage.chatRoom.roomId.eq(chatRoomId))
                .fetch();
    }
}
