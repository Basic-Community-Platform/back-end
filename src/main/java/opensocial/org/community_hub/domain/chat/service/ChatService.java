package opensocial.org.community_hub.domain.chat.service;

import opensocial.org.community_hub.domain.chat.dto.ChatRoomResponse;
import opensocial.org.community_hub.domain.chat.entity.ChatMessage;
import opensocial.org.community_hub.domain.chat.entity.ChatRoom;
import opensocial.org.community_hub.domain.chat.repository.ChatMessageRepository;
import opensocial.org.community_hub.domain.chat.repository.ChatRoomRepository;
import opensocial.org.community_hub.domain.user.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatService(ChatMessageRepository chatMessageRepository, ChatRoomRepository chatRoomRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    // 채팅방 생성
    public ChatRoomResponse createChatRoom(String roomName) {
        ChatRoom chatRoom = new ChatRoom(roomName);
        chatRoomRepository.save(chatRoom);
        return new ChatRoomResponse(chatRoom.getRoomName());
    }

    // 특정 채팅방 조회
    public ChatRoomResponse findRoomById(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        return new ChatRoomResponse(chatRoom.getRoomName());
    }

    // 모든 채팅방 조회
    public List<ChatRoomResponse> findAllRooms() {
        return chatRoomRepository.findAll().stream()
                .map(room -> new ChatRoomResponse(room.getRoomName()))
                .collect(Collectors.toList());
    }

    // 특정 채팅방의 메시지 조회
    public List<ChatMessage> getMessagesByRoomId(Long roomId) {
        return chatMessageRepository.findByChatRoomId(roomId);
    }

    // 유저가 채팅방에 속해 있는지 검증하는 메서드
    public void verifyUserInRoom(User user, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // 유저가 해당 채팅방에 속해 있는지 확인
        if (!chatRoom.getUsers().contains(user)) {
            throw new IllegalArgumentException("User does not have access to this room");
        }
    }

    // 메시지 저장
    public void saveMessage(ChatMessage.MessageType type, String content, User user, Long roomId) {
        // 유저가 해당 방에 속해 있는지 검증
        verifyUserInRoom(user, roomId);

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        ChatMessage chatMessage = new ChatMessage(type, content, user, chatRoom);
        chatMessage.setTimestamp(LocalDateTime.now());
    }
}
