package opensocial.org.community_hub.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import opensocial.org.community_hub.config.JwtConfig;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    private final JwtConfig jwtConfig;

    public JwtTokenUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    // 서명에 사용할 키 반환
    private Key getSigningKey() {
        // UTF-8 인코딩을 적용하여 secretKey를 바이트 배열로 변환
        return Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8));
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

    // JWT 액세스 토큰 생성 (UserDetails 기반)
    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername(), jwtConfig.getAccessTokenExpireTime()); // 6시간 유효기간
    }

    // JWT 리프레시 토큰 생성 (UserDetails 기반)
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername(), jwtConfig.getRefreshTokenExpireTime()); // 7일 유효기간
    }

    // JWT 토큰 생성
    private String generateToken(String username, long expirationTime) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰의 유효성을 검사 (UserDetails와 비교)
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
