package opensocial.org.community_hub.domain.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import opensocial.org.community_hub.domain.chat.dto.ChatMessageResponse;
import opensocial.org.community_hub.domain.chat.entity.ChatMessage;
import opensocial.org.community_hub.domain.chat.entity.ChatRoom;
import opensocial.org.community_hub.domain.chat.entity.QChatMessage;
import opensocial.org.community_hub.domain.chat.entity.QChatRoom;
import opensocial.org.community_hub.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ChatMessageQueryRepositoryImpl implements ChatMessageQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ChatMessageQueryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    //필요없는 필드까지 조회해서 성능 저하
    //엔티티를 DTO로 변환하며 쓸 데 없이 메모리 소모
    //추후 수정할 것
    @Override
    public List<ChatMessageResponse> findMessagesByRoomId(Long chatRoomId) {
        QChatMessage chatMessage = QChatMessage.chatMessage;
        QUser user = QUser.user;

        // QueryDsl과 Fetch Join을 사용해 엔티티 조회
        List<ChatMessage> messages = queryFactory.selectFrom(chatMessage)
                .join(chatMessage.user, user).fetchJoin()  // Fetch Join으로 N+1 문제 해결
                .where(chatMessage.chatRoom.roomId.eq(chatRoomId))
                .fetch();

        // 엔티티를 DTO로 변환
        return messages.stream()
                .map(msg -> new ChatMessageResponse(
                        msg.getContent(),
                        msg.getUser().getName(),
                        msg.getTimestamp()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ChatRoom> findByIdWithUsers(Long roomId) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QUser user = QUser.user;

        ChatRoom result = queryFactory.selectFrom(chatRoom)
                .leftJoin(chatRoom.users, user).fetchJoin()  // JOIN FETCH로 users 로드
                .where(chatRoom.roomId.eq(roomId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
