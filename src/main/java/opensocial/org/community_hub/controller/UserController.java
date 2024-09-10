package opensocial.org.community_hub.controller;

import opensocial.org.community_hub.dto.TokenResponse;
import opensocial.org.community_hub.dto.RefreshTokenRequest;
import opensocial.org.community_hub.entity.User;
import opensocial.org.community_hub.service.CustomUserDetailsService;
import opensocial.org.community_hub.service.UserService;
import opensocial.org.community_hub.util.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
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
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        try {
            TokenResponse tokenResponse = userService.login(user);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", tokenResponse.getAccessToken());
            response.put("refreshToken", tokenResponse.getRefreshToken());

            return ResponseEntity.ok(response);
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
                return ResponseEntity.ok(new TokenResponse(newAccessToken, request.getRefreshToken()));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error refreshing token");
        }
    }


    // JWT로 보호된 사용자 정보 조회
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
