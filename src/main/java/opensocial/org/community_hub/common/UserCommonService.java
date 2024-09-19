package opensocial.org.community_hub.common;

import opensocial.org.community_hub.domain.user.entity.User;
import opensocial.org.community_hub.domain.user.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCommonService {
    private final UserService userService;

    public User getUserByUserDetails(UserDetails userDetails) {
        String loginId = userDetails.getUsername();
        return userService.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
