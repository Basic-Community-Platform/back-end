package opensocial.org.community_hub.domain.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDetailsResponse {
    private String profileImageUrl;
    private String name;
    private String loginId;
    private String email;
    private List<String> postTitles;
    private List<String> commentContents;

    public UserDetailsResponse(String profileImageUrl, String name, String loginId, String email, List<String> postTitles, List<String> commentContents) {
        this.profileImageUrl = profileImageUrl;
        this.name = name;
        this.loginId = loginId;
        this.email = email;
        this.postTitles = postTitles;
        this.commentContents = commentContents;
    }
}