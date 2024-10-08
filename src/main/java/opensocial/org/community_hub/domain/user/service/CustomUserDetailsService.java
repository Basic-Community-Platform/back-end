package opensocial.org.community_hub.domain.user.service;

import opensocial.org.community_hub.domain.user.entity.User;
import opensocial.org.community_hub.domain.user.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //userRepository.findByLoginId(loginId): 일반적인 사용자 정보 조회 및 서비스 로직에서 사용.
    //customUserDetailsService.loadUserByUsername(loginId): Spring Security의 인증 및 권한 부여 로직에서 사용.
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByLoginId(loginId);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with loginId: " + loginId);
        }

        // 사용자 권한 설정
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new org.springframework.security.core.userdetails.User(
                user.get().getLoginId(),
                user.get().getPassword(),
                authorities
        );
    }
}
