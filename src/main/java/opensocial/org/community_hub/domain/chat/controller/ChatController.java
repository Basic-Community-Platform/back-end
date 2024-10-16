package opensocial.org.community_hub.domain.chat.controller;

import opensocial.org.community_hub.domain.chat.dto.ChatRoomResponse;
import opensocial.org.community_hub.domain.chat.dto.ChatRoomRequest;
import opensocial.org.community_hub.domain.chat.entity.ChatMessage;
import opensocial.org.community_hub.domain.chat.service.ChatService;
import opensocial.org.community_hub.domain.user.entity.User;
import opensocial.org.community_hub.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @PostMapping("/room/{roomId}/message")
    public ResponseEntity<String> sendMessage(
            @PathVariable Long roomId,
            @RequestParam String message,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 유저 정보 조회 및 검증
        User user = userService.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found with loginId: " + userDetails.getUsername()));

        // 권한 검증 로직 추가 (optional)
        chatService.verifyUserInRoom(user, roomId);

        // 메시지 저장
        chatService.saveMessage(ChatMessage.MessageType.CHAT, message, user, roomId);

        return ResponseEntity.ok("Message sent successfully.");
    }

    @PostMapping("/room")
    public ChatRoomResponse createRoom(@RequestBody ChatRoomRequest request) {
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
    public List<ChatMessage> getMessages(@PathVariable Long roomId) {
        return chatService.getMessagesByRoomId(roomId);
    }
}
