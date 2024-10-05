package opensocial.org.community_hub.domain.chat.controller;

import opensocial.org.community_hub.domain.chat.dto.ChatRoomDto;
import opensocial.org.community_hub.domain.chat.entity.ChatMessage;
import opensocial.org.community_hub.domain.chat.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    //추후에 요청 DTO 추가해서 변경하기
    @PostMapping("/room")
    public ChatRoomDto createRoom(@RequestBody Map<String, String> request) {
        return chatService.createChatRoom(request.get("name"));
    }


    @GetMapping("/rooms")
    public List<ChatRoomDto> getAllRooms() {
        return chatService.findAllRooms();
    }

    @GetMapping("/room/{roomId}")
    public ChatRoomDto getRoom(@PathVariable String roomId) {
        return chatService.findRoomById(roomId);
    }

    @GetMapping("/room/{roomId}/messages")
    public List<ChatMessage> getMessages(@PathVariable String roomId) {
        return chatService.getMessagesByRoomId(roomId);
    }
}
