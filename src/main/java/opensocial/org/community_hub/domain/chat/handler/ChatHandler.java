package opensocial.org.community_hub.domain.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import opensocial.org.community_hub.domain.chat.dto.ChatMessageRequest;
import opensocial.org.community_hub.domain.chat.service.ChatService;
import opensocial.org.community_hub.domain.user.entity.User;
import opensocial.org.community_hub.domain.user.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class ChatHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    // 해당 webSocket 세션에 연결된 채팅방
    private final Map<WebSocketSession, Long> sessionRoomMap = Collections.synchronizedMap(new HashMap<>());
    // 해당 webSocket 세션에 연결된 유저
    private final Map<WebSocketSession, User> sessionUserMap = Collections.synchronizedMap(new HashMap<>());

    public ChatHandler(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    // 커넥션 이후 해당 세션맵에 유저, 채팅방 각각 할당
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");
        Long roomId = (Long) session.getAttributes().get("roomId");

        System.out.println("Session Username: " + username);
        System.out.println("Session Room ID: " + roomId);

        if (username != null && roomId != null) {
            User user = userService.findByLoginId(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            sessionUserMap.put(session, user);
            sessionRoomMap.put(session, roomId);

            System.out.println("User " + username + " connected to room " + roomId);
        } else {
            session.sendMessage(new TextMessage("Connection closed: Missing user information or room ID"));
            session.close();  // 유저 정보나 roomId가 없으면 연결 닫기
        }
    }
    
    //맵에서 유저, 채팅방 가져온 뒤 유저-채팅방 매칭 검증 후 메시지 저장
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ChatMessageRequest chatMessageRequest = objectMapper.readValue(payload, ChatMessageRequest.class);

        User user = sessionUserMap.get(session);
        Long roomId = sessionRoomMap.get(session);

        if (user == null || roomId == null) {
            throw new IllegalArgumentException("User or Room is not authenticated.");
        }

        chatService.verifyUserInRoom(user, roomId);
        chatService.saveMessage(chatMessageRequest.getType(), chatMessageRequest.getContent(), user, roomId);

        broadcastMessageToRoom(roomId, message);
    }

    // 모든 사용자에게 메시지 전송 (Broadcast)
    private void broadcastMessageToRoom(Long roomId, TextMessage message) {
        sessionRoomMap.forEach((session, sessionRoomId) -> {
            if (sessionRoomId.equals(roomId) && session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 커넥션 종료 후, 해당 세션 삭제 및 해당 세션의 채팅방, 유저 삭제
    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session);
        sessionRoomMap.remove(session);
        sessionUserMap.remove(session);
        System.out.println("Session closed: " + session.getId());
    }
}
