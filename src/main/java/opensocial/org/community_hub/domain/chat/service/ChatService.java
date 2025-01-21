package opensocial.org.community_hub.domain.chat.service;

import jakarta.transaction.Transactional;
import opensocial.org.community_hub.domain.chat.dto.ChatMessageResponse;
import opensocial.org.community_hub.domain.chat.dto.ChatRoomResponse;
import opensocial.org.community_hub.domain.chat.entity.ChatMessage;
import opensocial.org.community_hub.domain.chat.entity.ChatRoom;
import opensocial.org.community_hub.domain.chat.repository.ChatMessageQueryRepositoryImpl;
import opensocial.org.community_hub.domain.chat.repository.ChatMessageRepository;
import opensocial.org.community_hub.domain.chat.repository.ChatRoomRepository;
import opensocial.org.community_hub.domain.user.entity.User;
import opensocial.org.community_hub.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageQueryRepositoryImpl chatMessageQueryRepository;
    private final UserRepository userRepository;

    public ChatService(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository, ChatMessageQueryRepositoryImpl chatMessageQueryRepository, UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatMessageQueryRepository = chatMessageQueryRepository;
        this.userRepository = userRepository;
    }

    // 특정 채팅방 조회
    public ChatRoomResponse findRoomById(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // 메시지 중 가장 이른 시간과 마지막 시간을 가져오기
        Optional<ChatMessage> firstMessage = chatMessageRepository.findFirstByChatRoomOrderByTimestampAsc(chatRoom);
        Optional<ChatMessage> lastMessage = chatMessageRepository.findFirstByChatRoomOrderByTimestampDesc(chatRoom);

        // 채팅방의 대표 유저 이미지 URL 설정 (첫 번째 유저 기준)
        String profileImageUrl = chatRoom.getUsers().stream()
                .findFirst() // 첫 번째 유저 선택
                .map(User::getProfileImageUrl) // 이미지 URL 가져오기
                .orElse(null); // 유저가 없으면 null 반환

        return ChatRoomResponse.builder()
                .roomId(chatRoom.getRoomId())
                .name(chatRoom.getRoomName())
                .profileImageUrl(profileImageUrl)
                .createdAt(firstMessage.map(ChatMessage::getTimestamp).orElse(null))
                .updatedAt(lastMessage.map(ChatMessage::getTimestamp).orElse(null))
                .build();
    }

    // 모든 채팅방 조회
    public List<ChatRoomResponse> findAllRooms() {
        return chatRoomRepository.findAll().stream()
                .map(room -> {
                    Optional<ChatMessage> firstMessage = chatMessageRepository.findFirstByChatRoomOrderByTimestampAsc(room);
                    Optional<ChatMessage> lastMessage = chatMessageRepository.findFirstByChatRoomOrderByTimestampDesc(room);

                    // 채팅방의 대표 유저 이미지 URL 설정 (첫 번째 유저 기준)
                    String profileImageUrl = room.getUsers().stream()
                            .findFirst()
                            .map(User::getProfileImageUrl)
                            .orElse(null);

                    return ChatRoomResponse.builder()
                            .roomId(room.getRoomId())
                            .name(room.getRoomName())
                            .profileImageUrl(profileImageUrl)
                            .createdAt(firstMessage.map(ChatMessage::getTimestamp).orElse(null))
                            .updatedAt(lastMessage.map(ChatMessage::getTimestamp).orElse(null))
                            .build();
                })
                .collect(Collectors.toList());
    }
}
