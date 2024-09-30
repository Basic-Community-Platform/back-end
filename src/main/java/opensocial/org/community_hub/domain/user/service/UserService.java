package opensocial.org.community_hub.domain.user.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensocial.org.community_hub.domain.user.dto.LoginResponse;
import opensocial.org.community_hub.domain.user.entity.User;
import opensocial.org.community_hub.domain.user.repository.UserRepository;
import opensocial.org.community_hub.util.JwtTokenUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    @Getter
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil; // JWT 유틸리티 주입
    private final CustomUserDetailsService customUserDetailsService;

    public User registerUser(User user) {
        User newUser = new User(user.getLoginId(), passwordEncoder.encode(user.getPassword()), user.getName(), user.getEmail(), user.getProfileImageUrl());

        return userRepository.save(newUser);
    }

    public Optional<User> findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    // 사용자 로그인 및 토큰 생성 (UserDetails 기반)
    public LoginResponse login(String loginId, String password) {
        // loginId로 UserDetailsService를 통해 사용자 조회
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginId);

        // 사용자 존재 여부 확인
        if (userDetails == null) {
            throw new RuntimeException("Login ID does not exist");
        }

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            log.warn("Invalid password for user: {}", loginId);
            throw new RuntimeException("Invalid password");
        }

        log.info("Password matches for user: {}. Generating JWT...", loginId);

        // JWT 토큰 생성
        String accessToken;
        String refreshToken;
        try {
            accessToken = jwtTokenUtil.generateToken(userDetails);
            refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
        } catch (Exception e) {
            log.error("Error during JWT token generation for user: {}", loginId, e);
            throw new RuntimeException("Error during JWT token generation", e);
        }

        // loginId로 사용자 조회
        User user = findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("User not found for login ID: " + loginId));

        // 로그인 응답 생성
        return new LoginResponse(accessToken, refreshToken, user.getLoginId(), user.getName(), user.getEmail(), user.getProfileImageUrl());
    }

    public User getUserByUserDetails(UserDetails userDetails) {
        String loginId = userDetails.getUsername();
        return findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
