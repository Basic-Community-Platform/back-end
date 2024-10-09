package opensocial.org.community_hub.domain.user.controller;

import opensocial.org.community_hub.domain.user.dto.LoginRequest;
import opensocial.org.community_hub.domain.user.dto.LoginResponse;
import opensocial.org.community_hub.domain.user.dto.RefreshTokenRequest;
import opensocial.org.community_hub.domain.user.entity.User;
import opensocial.org.community_hub.domain.user.service.CustomUserDetailsService;
import opensocial.org.community_hub.domain.user.service.UserService;
import opensocial.org.community_hub.util.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    public UserController(UserService userService, CustomUserDetailsService customUserDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        Optional<User> existingUser = userService.findByLoginId(user.getLoginId());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Login ID already exists");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Password cannot be empty");
        }

        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = userService.login(loginRequest.getLoginId(), loginRequest.getPassword());

            // Access Token 쿠키 설정
            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", loginResponse.getAccessToken())
                    .httpOnly(true)  // JavaScript에서 접근하지 못하도록 설정
                    .secure(true)    // HTTPS를 통해서만 전송 (개발 환경에서는 false로 설정할 수 있음)
                    .path("/")
                    .maxAge(24 * 60 * 60)  // 쿠키 유효기간 설정 (초 단위)
                    .sameSite("Strict")   // 쿠키의 SameSite 설정
                    .build();

            // Refresh Token 쿠키 설정
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", loginResponse.getRefreshToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)  // Refresh Token은 더 긴 유효기간을 가짐
                    .sameSite("Strict")
                    .build();

            // 쿠키를 응답 헤더에 추가하여 클라이언트에게 전달
            return ResponseEntity.ok()
                    .header("Set-Cookie", accessTokenCookie.toString())
                    .header("Set-Cookie", refreshTokenCookie.toString())
                    .body("Login successful");
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Invalid login credentials");
        }
    }

    //액세스 토큰 만료 시 리프레쉬 토큰으로 액세스 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            String username = jwtTokenUtil.extractUsername(request.getRefreshToken());

            // 유효한 리프레시 토큰인지 검증
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(request.getRefreshToken(), userDetails)) {
                String newAccessToken = jwtTokenUtil.generateToken(userDetails);
                return ResponseEntity.ok(newAccessToken);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error refreshing token");
        }
    }
}
