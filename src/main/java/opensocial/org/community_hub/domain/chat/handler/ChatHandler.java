package opensocial.org.community_hub.domain.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import opensocial.org.community_hub.domain.chat.dto.ChatMessageDto;
import opensocial.org.community_hub.domain.chat.entity.ChatMessage;
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

        // JSON 메시지를 ChatMessageDto 객체로 파싱
        ChatMessageDto chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);

        // 세션에서 사용자 정보 가져오기 (JWT 또는 인증 정보를 이용하여 세션 관리)
        UserDetails userDetails = (UserDetails) session.getPrincipal();
        User user = userService.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found with loginId: " + userDetails.getUsername()));

        Long roomId = Long.parseLong(chatMessageDto.getRoomId());

        // 유저가 해당 채팅방에 속해 있는지 확인
        chatService.verifyUserInRoom(user, roomId);

        // 메시지 저장
        chatService.saveMessage(
                ChatMessage.MessageType.valueOf(chatMessageDto.getType()),
                chatMessageDto.getContent(),
                user,
                roomId
        );

        // 채팅방에 속한 사용자들에게만 메시지 브로드캐스트
        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen() && sessionRoomMap.get(webSocketSession).equals(roomId)) {
                // chatMessageDto를 JSON으로 변환 후 전송
                String broadcastMessage = objectMapper.writeValueAsString(chatMessageDto);
                webSocketSession.sendMessage(new TextMessage(broadcastMessage));
            }
        }

        // 세션이 속한 채팅방 정보 저장
        sessionRoomMap.put(session, roomId);
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
