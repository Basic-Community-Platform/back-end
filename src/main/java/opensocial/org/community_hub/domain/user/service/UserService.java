package opensocial.org.community_hub.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensocial.org.community_hub.domain.user.dto.RegisterRequest;
import opensocial.org.community_hub.domain.user.dto.UserDetailsResponse;
import opensocial.org.community_hub.domain.user.entity.User;
import opensocial.org.community_hub.domain.user.exception.UserNotFoundException;
import opensocial.org.community_hub.domain.user.repository.UserQueryRepository;
import opensocial.org.community_hub.domain.user.repository.UserRepository;
import opensocial.org.community_hub.util.JwtTokenUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserQueryRepository userQueryRepository;

    // 회원 가입 메서드
    public void registerUser(RegisterRequest registerRequest) {
        if (existsByLoginId(registerRequest.getLoginId())) {
            log.warn("Registration failed. Login ID already exists: {}", registerRequest.getLoginId());
            throw new IllegalArgumentException("Login ID already exists");
        }

        User newUser = new User(registerRequest.getLoginId(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getName(),
                registerRequest.getEmail(),
                registerRequest.getProfileImageUrl());

        userRepository.save(newUser);
        log.info("User registered successfully: {}", registerRequest.getLoginId());
    }

    // 로그인 메서드 (JWT 토큰 생성만 처리)
    public Map<String, String> login(String loginId, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginId);

        // 사용자 유효성 검증
        validateUser(userDetails, password);

        // JWT 토큰 생성
        String accessToken = jwtTokenUtil.generateToken(userDetails);
        String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
        log.info("JWT Token generated for user: {}", loginId);

        // AccessToken과 RefreshToken을 Map으로 반환
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
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

    public UserDetailsResponse getUserDetails(String userId) {
        return userQueryRepository.findUserDetailsByLoginId(userId);
    }
}
