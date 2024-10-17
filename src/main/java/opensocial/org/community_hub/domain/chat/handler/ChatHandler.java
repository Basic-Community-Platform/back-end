package opensocial.org.community_hub.domain.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import opensocial.org.community_hub.domain.chat.dto.ChatMessageRequest;
import opensocial.org.community_hub.domain.chat.service.ChatService;
import opensocial.org.community_hub.domain.user.entity.User;
import opensocial.org.community_hub.domain.user.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final ObjectMapper objectMapper; // Jackson ObjectMapper를 이용하여 JSON 파싱
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    // 세션별로 속한 채팅방 관리 (세션 ID -> 채팅방 ID)
    private final Map<WebSocketSession, Long> sessionRoomMap = Collections.synchronizedMap(new HashMap<>());

    public ChatHandler(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    // 새로운 클라이언트가 WebSocket을 통해 연결되었을 때 호출
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session); // 연결된 세션 추가
        System.out.println("New session connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ChatMessageRequest chatMessageRequest = objectMapper.readValue(payload, ChatMessageRequest.class);

        // 유저 정보 가져오기 (UserDetails에서 추출)
        UserDetails userDetails = (UserDetails) session.getPrincipal();
        User user = userService.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 채팅방에 속해 있는지 검증
        chatService.verifyUserInRoom(user, chatMessageRequest.getRoomId());

        // 메시지 저장
        chatService.saveMessage(chatMessageRequest.getType(), chatMessageRequest.getContent(), user, chatMessageRequest.getRoomId());

        // 모든 사용자에게 메시지 전송 (Broadcast)
        broadcastMessageToRoom(chatMessageRequest.getRoomId(), message);
    }

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

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session);
        sessionRoomMap.remove(session);
        System.out.println("Session closed: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Error occurred in WebSocket session: " + session.getId() + " Error: " + exception.getMessage());
        session.close();
        sessions.remove(session);
        sessionRoomMap.remove(session);
    }
}
