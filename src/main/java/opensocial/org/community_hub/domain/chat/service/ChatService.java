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

    // 채팅방 생성
    public ChatRoomResponse createChatRoom(String roomName) {
        ChatRoom chatRoom = new ChatRoom(roomName);
        chatRoomRepository.save(chatRoom);
        return new ChatRoomResponse(chatRoom.getRoomId(), chatRoom.getRoomName());
    }

    // 특정 채팅방 조회
    public ChatRoomResponse findRoomById(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        return new ChatRoomResponse(chatRoom.getRoomId(), chatRoom.getRoomName());
    }

    // 모든 채팅방 조회
    public List<ChatRoomResponse> findAllRooms() {
        return chatRoomRepository.findAll().stream()
                .map(room -> new ChatRoomResponse(room.getRoomId(), room.getRoomName()))
                .collect(Collectors.toList());
    }

    // 특정 채팅방의 메시지 조회
    public List<ChatMessageResponse> getMessagesByRoomId(Long roomId) {
        return chatMessageQueryRepository.findMessagesByRoomId(roomId);
    }

    @Transactional  // 트랜잭션 활성화
    public void saveMessage(ChatMessage.MessageType type, String content, User user, Long roomId) {
        // 유저가 해당 방에 속해 있는지 검증
        verifyUserInRoom(user, roomId);

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        ChatMessage chatMessage = new ChatMessage(type, content, user, chatRoom);
        chatMessage.setTimestamp(LocalDateTime.now());

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
