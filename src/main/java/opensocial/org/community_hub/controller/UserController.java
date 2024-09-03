package opensocial.org.community_hub.controller;

import opensocial.org.community_hub.entity.User;
import opensocial.org.community_hub.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        // 추후 예외처리 코드 추가
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        User newUser = userService.registerUser(user);
        return ResponseEntity.ok(newUser);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        User existingUser = userService.findByLoginId(user.getLoginId());
        if (existingUser != null && userService.getPasswordEncoder().matches(user.getPassword(), existingUser.getPassword())) {
            return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.status(401).body("Invalid login credentials");
    }

    // 사용자 정보 조회 (테스트 코드, 추후 수정)
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
