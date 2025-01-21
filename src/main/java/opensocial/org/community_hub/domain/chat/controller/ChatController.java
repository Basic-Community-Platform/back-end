package opensocial.org.community_hub.domain.chat.controller;

import lombok.extern.slf4j.Slf4j;
import opensocial.org.community_hub.domain.chat.dto.ChatMessageResponse;
import opensocial.org.community_hub.domain.chat.dto.ChatRoomResponse;
import opensocial.org.community_hub.domain.chat.dto.ChatRoomRequest;
import opensocial.org.community_hub.domain.chat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/room")
    public ChatRoomResponse createRoom(@RequestBody ChatRoomRequest request) {
        log.debug("Received POST request to create room: {}", request.getName());
        return chatService.createChatRoom(request.getName());
    }

    @GetMapping("/rooms")
    public List<ChatRoomResponse> getAllRooms() {
        return chatService.findAllRooms();
    }

    @GetMapping("/room/{roomId}")
    public ChatRoomResponse getRoom(@PathVariable Long roomId) {
        return chatService.findRoomById(roomId);
    }

    @GetMapping("/room/{roomId}/messages")
    public List<ChatMessageResponse> getMessages(@PathVariable Long roomId, @AuthenticationPrincipal UserDetails userDetails) {
        String loginId = userDetails.getUsername(); // UserDetails에서 loginId 가져오기
        return chatService.getMessagesByRoomId(roomId, loginId);
    }

    // 채팅방에 유저 추가
    @PostMapping("/room/{roomId}/addUser/{userId}")
    public ResponseEntity<String> addUserToRoom(@PathVariable Long roomId, @PathVariable Long userId) {

        chatService.addUserToRoom(roomId, userId);
        return ResponseEntity.ok("User added to the room successfully.");
    }

    // 채팅방에서 유저 제거
    @DeleteMapping("/room/{roomId}/removeUser/{userId}")
    public ResponseEntity<String> removeUserFromRoom(@PathVariable Long roomId, @PathVariable Long userId) {

        chatService.removeUserFromRoom(roomId, userId);
        return ResponseEntity.ok("User removed from the room successfully.");
    }
}
