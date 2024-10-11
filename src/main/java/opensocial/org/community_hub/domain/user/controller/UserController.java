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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
        // 사용자 등록 요청 처리
        if (userService.existsByLoginId(user.getLoginId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Login ID already exists");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password cannot be empty");
        }

        userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<Void> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = userService.login(loginRequest.getLoginId(), loginRequest.getPassword());

            // Access Token 및 Refresh Token 쿠키 설정
            ResponseCookie accessTokenCookie = createCookie("accessToken", loginResponse.getAccessToken(), 24 * 60 * 60);
            ResponseCookie refreshTokenCookie = createCookie("refreshToken", loginResponse.getRefreshToken(), 7 * 24 * 60 * 60);

            return ResponseEntity.ok()
                    .header("Set-Cookie", accessTokenCookie.toString())
                    .header("Set-Cookie", refreshTokenCookie.toString())
                    .body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            String username = jwtTokenUtil.extractUsername(request.getRefreshToken());

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(request.getRefreshToken(), userDetails)) {
                String newAccessToken = jwtTokenUtil.generateToken(userDetails);
                return ResponseEntity.ok(newAccessToken);  // 새 Access Token 문자열 반환
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error refreshing token");
        }
    }

    // 공통적으로 사용하는 쿠키 생성 메서드
    private ResponseCookie createCookie(String name, String value, int maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
    }
}
