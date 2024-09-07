package opensocial.org.community_hub.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import opensocial.org.community_hub.entity.User;
import opensocial.org.community_hub.repository.UserRepository;
import opensocial.org.community_hub.util.JwtTokenUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    @Getter
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil; // JWT 유틸리티 주입

    public User registerUser(User user) {
        // 비밀번호 인코딩
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public String login(User user) {
        Optional<User> existingUser = userRepository.findByLoginId(user.getLoginId());

        if (!existingUser.isPresent()) {
            throw new RuntimeException("Login ID does not exist");
        }

        // 비밀번호가 일치하는지 확인
        if (passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {
            System.out.println("Password matches! Generating JWT...");
            try {
                // JWT 토큰 발급
                // 현재 loginId로 토큰 발급
                String token = jwtTokenUtil.generateToken(existingUser.get().getLoginId());

                System.out.println("Generated JWT Token: " + token); // 토큰이 제대로 생성되는지 확인
                return token;
            } catch (Exception e) {
                // 예외가 발생하면 로그 출력
                e.printStackTrace();
                throw new RuntimeException("Error during JWT token generation");
            }
        } else {
            System.out.println("Password does not match!");
            throw new RuntimeException("Invalid password");
        }
    }
}
