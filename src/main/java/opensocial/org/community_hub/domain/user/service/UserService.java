package opensocial.org.community_hub.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensocial.org.community_hub.domain.user.dto.LoginResponse;
import opensocial.org.community_hub.domain.user.entity.User;
import opensocial.org.community_hub.domain.user.exception.UserNotFoundException;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;

    // 회원 가입 메서드
    public void registerUser(User user) {
        if (existsByLoginId(user.getLoginId())) {
            log.warn("Registration failed. Login ID already exists: {}", user.getLoginId());
            throw new IllegalArgumentException("Login ID already exists");
        }

        User newUser = new User(user.getLoginId(),
                passwordEncoder.encode(user.getPassword()),
                user.getName(),
                user.getEmail(),
                user.getProfileImageUrl());

        userRepository.save(newUser);
        log.info("User registered successfully: {}", user.getLoginId());
    }

    // 로그인 메서드 (JWT 토큰 생성)
    public LoginResponse login(String loginId, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginId);

        // 사용자 유효성 검증
        validateUser(userDetails, password);

        // JWT 토큰 생성
        String accessToken = jwtTokenUtil.generateToken(userDetails);
        String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
        log.info("JWT Token generated for user: {}", loginId);

        // 로그인 응답 생성
        return new LoginResponse(accessToken, refreshToken);
    }

    // 사용자 로그인 ID로 조회
    public Optional<User> findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }

    // UserDetails를 기반으로 User 엔터티 조회
    public User getUserByUserDetails(UserDetails userDetails) {
        return findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found with login ID: " + userDetails.getUsername()));
    }

    // 사용자 존재 여부 확인
    public boolean existsByLoginId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    // 사용자 비밀번호 및 존재 여부 검증 메서드
    private void validateUser(UserDetails userDetails, String rawPassword) {
        if (userDetails == null) {
            log.warn("Login failed. User not found with login ID: {}", rawPassword);
            throw new UserNotFoundException("Login ID does not exist");
        }

        if (!passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
            log.warn("Invalid password for user: {}", userDetails.getUsername());
            throw new RuntimeException("Invalid password");
        }
    }
}
