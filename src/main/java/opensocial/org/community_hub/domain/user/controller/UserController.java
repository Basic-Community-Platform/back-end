package opensocial.org.community_hub.domain.user.controller;

import opensocial.org.community_hub.domain.user.dto.LoginRequest;
import opensocial.org.community_hub.domain.user.dto.RefreshTokenRequest;
import opensocial.org.community_hub.domain.user.dto.RegisterRequest;
import opensocial.org.community_hub.domain.user.dto.UserDetailsResponse;
import opensocial.org.community_hub.domain.user.entity.User;
import opensocial.org.community_hub.domain.user.service.CustomUserDetailsService;
import opensocial.org.community_hub.domain.user.service.UserService;
import opensocial.org.community_hub.util.JwtTokenUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            userService.registerUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            Map<String, String> tokens = userService.login(loginRequest.getLoginId(), loginRequest.getPassword());

            // Access Token은 인증 헤더에 세팅
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + tokens.get("accessToken"));
            // Refresh Token은 쿠키에 세팅
            ResponseCookie refreshTokenCookie = createCookie("refreshToken", tokens.get("refreshToken"), 7 * 24 * 60 * 60);

            return ResponseEntity.ok()
                    .headers(headers)
                    .header("Set-Cookie", refreshTokenCookie.toString())
                    .body("Login successful");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            String username = jwtTokenUtil.extractUsername(refreshTokenRequest.getRefreshToken());

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(refreshTokenRequest.getRefreshToken(), userDetails)) {
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

    //유저 상세정보 - 마이페이지용
    //권한 필요
    @GetMapping("/details")
    public ResponseEntity<UserDetailsResponse> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUserDetails(userDetails);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // 로그인된 사용자의 기본 정보를 조회
        UserDetailsResponse response = userService.getUserDetails(user.getLoginId());
        if (response == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }


    //유저 상세정보 - 일반적인 외부 열람용
    //권한 불필요
}
