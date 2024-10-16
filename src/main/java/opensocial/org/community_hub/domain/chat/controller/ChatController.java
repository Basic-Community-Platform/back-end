package opensocial.org.community_hub.domain.chat.controller;

import opensocial.org.community_hub.domain.chat.dto.ChatMessageRequest;
import opensocial.org.community_hub.domain.chat.dto.ChatMessageResponse;
import opensocial.org.community_hub.domain.chat.dto.ChatRoomResponse;
import opensocial.org.community_hub.domain.chat.dto.ChatRoomRequest;
import opensocial.org.community_hub.domain.chat.entity.ChatMessage;
import opensocial.org.community_hub.domain.chat.entity.ChatRoom;
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

    @PostMapping("/room/{roomId}/message")
    public ResponseEntity<String> sendMessage(
            @PathVariable Long roomId,
            @RequestBody ChatMessageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 유저 정보 조회 및 검증
        User user = userService.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found with loginId: " + userDetails.getUsername()));

        // 채팅방 조회 및 검증
        ChatRoomResponse chatRoom = chatService.findRoomById(roomId);

        // 해당 유저의 채팅방 접근 권한 검증
        chatService.verifyUserInRoom(user, roomId);

        // 메시지 저장
        chatService.saveMessage(request.getType(), request.getContent(), user, chatRoom.getRoomId());

        return ResponseEntity.ok("Message sent successfully.");
    }

    @GetMapping("/room/{roomId}/messages")
    public List<ChatMessageResponse> getMessages(@PathVariable Long roomId) {
        return chatService.getMessagesByRoomId(roomId);
    }

    // 채팅방에 유저 추가
    @PostMapping("/room/{roomId}/addUser/{userId}")
    public ResponseEntity<String> addUserToRoom(
            @PathVariable Long roomId,
            @PathVariable Long userId) {

        chatService.addUserToRoom(roomId, userId);
        return ResponseEntity.ok("User added to the room successfully.");
    }

    // 채팅방에서 유저 제거
    @DeleteMapping("/room/{roomId}/removeUser/{userId}")
    public ResponseEntity<String> removeUserFromRoom(
            @PathVariable Long roomId,
            @PathVariable Long userId) {

        chatService.removeUserFromRoom(roomId, userId);
        return ResponseEntity.ok("User removed from the room successfully.");
    }
}
