package opensocial.org.community_hub.domain.user.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import opensocial.org.community_hub.domain.user.dto.TokenResponse;
import opensocial.org.community_hub.domain.user.entity.User;
import opensocial.org.community_hub.domain.user.repository.UserRepository;
import opensocial.org.community_hub.util.JwtTokenUtil;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final CustomUserDetailsService customUserDetailsService;

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

    // 사용자 로그인 및 토큰 생성 (UserDetails 기반)
    public TokenResponse login(User user) {
        // loginId로 UserDetailsService를 통해 사용자 조회
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getLoginId());

        if (userDetails == null) {
            throw new RuntimeException("Login ID does not exist");
        }

        // 비밀번호 일치 여부 확인
        if (passwordEncoder.matches(user.getPassword(), userDetails.getPassword())) {
            System.out.println("Password matches! Generating JWT...");
            try {
                // UserDetails를 기반으로 JWT 생성
                String accessToken = jwtTokenUtil.generateToken(userDetails);
                String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

                return new TokenResponse(accessToken, refreshToken);
            } catch (Exception e) {
                e.printStackTrace(); // 예외가 발생하면 로그 출력
                throw new RuntimeException("Error during JWT token generation");
            }
        } else {
            System.out.println("Password does not match!");
            throw new RuntimeException("Invalid password");
        }
    }
}
