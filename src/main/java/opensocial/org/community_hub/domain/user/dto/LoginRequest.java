package opensocial.org.community_hub.domain.user.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String loginId;
    private String password;
}
