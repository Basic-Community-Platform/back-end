package opensocial.org.community_hub.domain.chat.service;

import opensocial.org.community_hub.domain.chat.dto.ChatRoomDto;
import opensocial.org.community_hub.domain.chat.entity.ChatRoom;
import opensocial.org.community_hub.domain.chat.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    public ChatRoomDto createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        chatRoomRepository.save(chatRoom);
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getName());
    }

    public List<ChatRoomDto> findAllRooms() {
        return chatRoomRepository.findAll().stream()
                .map(room -> new ChatRoomDto(room.getId(), room.getName()))
                .collect(Collectors.toList());
    }

    public ChatRoomDto findRoomById(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getName());
    }
}
