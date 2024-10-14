package opensocial.org.community_hub.domain.chat.service;

import opensocial.org.community_hub.domain.chat.dto.ChatRoomDto;
import opensocial.org.community_hub.domain.chat.entity.ChatMessage;
import opensocial.org.community_hub.domain.chat.entity.ChatRoom;
import opensocial.org.community_hub.domain.chat.repository.ChatMessageRepository;
import opensocial.org.community_hub.domain.chat.repository.ChatRoomRepository;
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

    public ChatRoomDto createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        chatRoomRepository.save(chatRoom);
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getName());
    }

    public ChatRoomDto findRoomById(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getName());
    }

    public List<ChatRoomDto> findAllRooms() {
        return chatRoomRepository.findAll().stream()
                .map(room -> new ChatRoomDto(room.getId(), room.getName()))
                .collect(Collectors.toList());
    }

    public ChatMessage saveMessage(ChatMessage.MessageType type, String content, String sender, String roomId) {
        // 해당 roomId로 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // ChatMessage 객체 생성 시 현재 시간을 설정하여 timestamp 필드를 초기화
        ChatMessage chatMessage = new ChatMessage(type, content, sender, chatRoom);
        chatMessage.setTimestamp(LocalDateTime.now());  // 현재 시간을 timestamp로 설정

        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getMessagesByRoomId(String roomId) {
        return chatMessageRepository.findByChatRoomId(roomId);
    }
}
