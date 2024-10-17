package opensocial.org.community_hub.config;

import opensocial.org.community_hub.util.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenUtil jwtTokenUtil;

    public JwtHandshakeInterceptor(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // Authorization 헤더에서 JWT 토큰 추출
        String token = request.getHeaders().getFirst("Authorization");
        System.out.println("JWT Token: " + token);  // JWT 토큰이 제대로 오는지 확인

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);  // 'Bearer ' 제거
            try {
                // JWT에서 사용자 이름 추출
                String username = jwtTokenUtil.extractUsername(token);

                if (username == null || username.isEmpty()) {
                    System.out.println("Username from JWT is missing or invalid");
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return false;
                }
                attributes.put("username", username);  // 여기서 username 저장
                System.out.println("Username from JWT: " + username); // 유저 이름 확인

                // 쿼리 파라미터에서 roomId 추출
                String query = request.getURI().getQuery();
                if (query != null && query.contains("roomId=")) {
                    String roomId = query.split("roomId=")[1].split("&")[0];
                    attributes.put("roomId", Long.parseLong(roomId));  // roomId 저장
                    System.out.println("Room ID from query: " + roomId);  // roomId 로그 확인
                    return true;
                } else {
                    System.out.println("Room ID not found in query");
                    response.setStatusCode(HttpStatus.BAD_REQUEST);
                    return false;  // roomId가 없으면 차단
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatusCode(HttpStatus.UNAUTHORIZED); // 401 Unauthorized
                return false;  // JWT 검증 실패 시 WebSocket 연결 차단
            }
        } else {
            System.out.println("Invalid JWT token");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);  // JWT 토큰이 없을 경우 연결 차단
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 핸드셰이크 후 작업
    }
}
