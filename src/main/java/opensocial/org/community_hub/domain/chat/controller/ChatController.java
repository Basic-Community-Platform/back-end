package opensocial.org.community_hub.domain.chat.controller;

import opensocial.org.community_hub.domain.chat.dto.ChatRoomDto;
import opensocial.org.community_hub.domain.chat.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/room")
    public ChatRoomDto createRoom(@RequestParam String name) {
        return chatService.createChatRoom(name);
    }

    @GetMapping("/rooms")
    public List<ChatRoomDto> getAllRooms() {
        return chatService.findAllRooms();
    }

    @GetMapping("/room/{roomId}")
    public ChatRoomDto getRoom(@PathVariable String roomId) {
        return chatService.findRoomById(roomId);
    }
}
