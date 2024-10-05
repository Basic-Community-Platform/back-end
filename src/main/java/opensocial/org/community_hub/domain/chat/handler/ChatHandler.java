package opensocial.org.community_hub.domain.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import opensocial.org.community_hub.domain.chat.dto.ChatMessageDto;
import opensocial.org.community_hub.domain.chat.entity.ChatMessage;
import opensocial.org.community_hub.domain.chat.service.ChatService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class ChatHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;  // Jackson ObjectMapper를 이용하여 JSON 파싱
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    public ChatHandler(ChatService chatService) {
        this.chatService = chatService;
        this.objectMapper = new ObjectMapper();  // ObjectMapper 인스턴스 생성
    }

    //새로운 클라이언트가 WebSocket을 통해 연결되었을 때 호출
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session); //연결된 세션 추가
        System.out.println("New session connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Message received: " + payload);

        // JSON 메시지를 ChatMessageDto 객체로 파싱
        ChatMessageDto chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);

        // DTO에서 필요한 정보를 추출하여 ChatMessage 객체에 저장
        chatService.saveMessage(
                ChatMessage.MessageType.valueOf(chatMessageDto.getType()),  // 메시지 타입
                chatMessageDto.getContent(),  // 메시지 내용
                chatMessageDto.getSender(),   // 메시지 보낸 사용자 이름
                chatMessageDto.getRoomId()    // 채팅방 ID2
        );

        // 모든 세션에 메시지 브로드캐스트
        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen()) {
                webSocketSession.sendMessage(new TextMessage(payload));  // 파싱된 메시지 그대로 브로드캐스트
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Session closed: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Error occurred in WebSocket session: " + session.getId() + " Error: " + exception.getMessage());
        session.close();
        sessions.remove(session);
    }
}
