package opensocial.org.community_hub.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // 서명에 사용할 키 반환
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // JWT에서 사용자 이름 추출 (해당 앱에선 loginId 추출)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); //토큰 발급 시(아래 generateToken() 매서드) setSubject에 세팅한 값으로 claim 설정됨
    }

    // 특정 클레임 추출
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 모든 클레임 추출
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    // JWT 토큰이 만료되었는지 확인
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // JWT 토큰의 만료 시간 추출
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // JWT 토큰 생성
    public String generateToken(String loginId) {
        return Jwts.builder()
                .setSubject(loginId)  // 사용자 이름 (또는 ID)
                .setIssuedAt(new Date(System.currentTimeMillis()))  // 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // 10시간 유효기간
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // 비밀 키로 서명
                .compact();
    }

    // JWT 토큰이 유효한지 확인
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
