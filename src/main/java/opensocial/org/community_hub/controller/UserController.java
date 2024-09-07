package opensocial.org.community_hub.controller;

import opensocial.org.community_hub.entity.User;
import opensocial.org.community_hub.service.UserService;
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

    public UserController(UserService userService) {
        this.userService = userService;
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
            String token = userService.login(user);  // JWT 토큰 생성

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", userService.findByLoginId(user.getLoginId()).get().getUserId());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Invalid login credentials");
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
