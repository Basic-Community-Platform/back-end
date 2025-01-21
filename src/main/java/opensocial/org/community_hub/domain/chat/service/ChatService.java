package opensocial.org.community_hub.domain.chat.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

    // 채팅방 생성
    public ChatRoomResponse createChatRoom(String roomName) {
        log.debug("Creating chat room: {}", roomName);
        ChatRoom chatRoom = new ChatRoom(roomName);
        chatRoomRepository.save(chatRoom);

        return ChatRoomResponse.builder()
                .roomId(chatRoom.getRoomId())
                .name(chatRoom.getRoomName())
                .profileImageUrl(null)
                .createdAt(null)
                .updatedAt(null)
                .build();
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

    // 특정 채팅방의 메시지 조회
    @Transactional
    public List<ChatMessageResponse> getMessagesByRoomId(Long roomId, String loginId) {
        List<ChatMessageResponse> messages = chatMessageQueryRepository.findMessagesByRoomId(roomId);

        // 각 메시지의 isMyMessage 값을 설정
        messages.forEach(message -> {
            boolean isMyMessage = message.getSenderLoginId().equals(loginId);
            message.setIsMyMessage(isMyMessage);
        });

        return messages;
    }

    @Transactional
    public void saveMessage(ChatMessage.MessageType type, String content, User user, Long roomId) {
        // 유저가 해당 방에 속해 있는지 검증
        verifyUserInRoom(user, roomId);

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // 수정된 생성자 사용
        ChatMessage chatMessage = new ChatMessage(type, content, user, chatRoom);

        chatMessageRepository.save(chatMessage);
    }

    @Transactional
    public void verifyUserInRoom(User user, Long roomId) {
        // QueryDSL로 users 컬렉션을 미리 로드
        ChatRoom chatRoom = chatMessageQueryRepository.findByIdWithUsers(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // 유저가 해당 채팅방에 속해 있는지 확인
        if (!chatRoom.getUsers().contains(user)) {
            throw new IllegalArgumentException("User does not have access to this room");
        }
    }

    // 채팅방에 유저 추가
    public void addUserToRoom(Long roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 유저가 이미 채팅방에 있는지 확인
        if (chatRoom.getUsers().contains(user)) {
            throw new IllegalArgumentException("User already in the chat room");
        }

        // 채팅방에 유저 추가
        chatRoom.addUser(user);
        chatRoomRepository.save(chatRoom); // 변경 사항 저장
    }

    // 채팅방에서 유저 제거
    public void removeUserFromRoom(Long roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 채팅방에서 유저 제거
        chatRoom.removeUser(user);
        chatRoomRepository.save(chatRoom); // 변경 사항 저장
    }
}
