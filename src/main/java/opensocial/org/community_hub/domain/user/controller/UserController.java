package opensocial.org.community_hub.domain.user.controller;

import opensocial.org.community_hub.domain.user.dto.LoginRequest;
import opensocial.org.community_hub.domain.user.dto.LoginResponse;
import opensocial.org.community_hub.domain.user.dto.RefreshTokenRequest;
import opensocial.org.community_hub.domain.user.entity.User;
import opensocial.org.community_hub.domain.user.service.CustomUserDetailsService;
import opensocial.org.community_hub.domain.user.service.UserService;
import opensocial.org.community_hub.util.JwtTokenUtil;
import org.springframework.http.HttpStatus;
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

            return ResponseEntity.ok(loginResponse);
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


    // JWT로 보호된 사용자 정보 조회
    // 추후에 해당 기능 사용 시 @PathVariable 없이 @AuthenticationPrincipal 로만 받도록 수정 필요
    // 현재는 프론트에서 전역 스토어에 필요한 정보 다 들고 있어서 사용 안 되는 중
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId, @AuthenticationPrincipal UserDetails currentUser) {
        System.out.println("Authenticated user: " + currentUser.getUsername());

        User user = userService.getUserById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }
}
