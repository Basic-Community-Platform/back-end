package opensocial.org.community_hub.domain.user.dto;

import lombok.Getter;

@Getter
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String loginId;
    private String name;
    private String email;
    private String profileImageUrl;

    public LoginResponse(String accessToken, String refreshToken, String loginId, String name, String email, String profileImageUrl) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.loginId = loginId;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }
}
